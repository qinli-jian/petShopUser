package com.example.petshopuser.controller;

import com.example.petshopuser.common.Constants;
import com.example.petshopuser.entity.Ip_address;
import com.example.petshopuser.entity.Address;
import com.example.petshopuser.entity.ReturnObj;
import com.example.petshopuser.entity.User;
import com.example.petshopuser.utils.IpUtil;
import com.example.petshopuser.utils.SnowflakeIdWorker;
import com.example.petshopuser.utils.Utils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.core.io.FileSystemResource;
import org.apache.coyote.Request;
import org.springframework.http.*;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import com.example.petshopuser.service.impl.UserServiceImpl;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    private final SnowflakeIdWorker snowflakeIdWorker;
    @Resource
    private UserServiceImpl userService;
    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    public UserController(SnowflakeIdWorker snowflakeIdWorker) {
        this.snowflakeIdWorker = snowflakeIdWorker;
    }
    @PostMapping("/login")
    public ReturnObj login(@RequestBody Map<String,String> login_form,HttpServletRequest request){
        ReturnObj returnObj = new ReturnObj();

        System.out.println(login_form);
        User user = userService.findUserByAccount(login_form.get("account"));
        if(user!=null){
            if(bCryptPasswordEncoder.matches(login_form.get("password"),user.getPassword())){
                System.out.println(request);
                Map<String,String> map = new HashMap<>();
                String res =  IpUtil.getIpAddr(request);
                StringBuilder result= new StringBuilder();
                int num=0;
                for(int i=0;i<res.length();i++){
                    if(res.charAt(i)=='|'){
                        num++;
                        if(num==4)
                            break;
                        continue;
                    }
                    if(res.charAt(i)!='0')
                        result.append(res.charAt(i));
                }
                if(!result.toString().equals("unknown")){
                    userService.setIP(String.valueOf(snowflakeIdWorker.nextId()), user.getId(),
                            request.getHeader("X-Real-IP"), result.toString());
                    map.put("ip", request.getHeader("X-Real-IP"));
                    map.put("res", result.toString());
                    returnObj.setMsg("位于: "+map.get("res")+",ip为: "+map.get("ip")+"的用户登陆成功");
                    // 判断是不是异地登录
                    userService.check_remoteLogin(user.getId());
                }
                else
                returnObj.setMsg("登陆成功");
                returnObj.setCode("200");
                String token = Utils.generateToken(user,"user");
                Map<String, Object> data = new HashMap<>();
                data.put("token",token);
                data.put("user",user);
                data.put("ip", map.get("ip"));
                data.put("ip_address", map.get("res"));
                returnObj.setData(data);
            }
            else{
                returnObj.setMsg("密码错误,请重试");
                returnObj.setCode("500");
            }
            return returnObj;
        }
        returnObj.setMsg("没有该用户,请注册！");
        returnObj.setCode("500");
        return returnObj;
    }

    @PostMapping("/register")
    public ReturnObj register(@RequestBody Map<String,String> register_form){
        ReturnObj returnObj = new ReturnObj();
//        String
        String username = register_form.get("username");
        String sex = register_form.get("sex");
        String age = register_form.get("age");
        String phone = register_form.get("phone");
        String code = register_form.get("code");
        String password = register_form.get("password");
        String repassword = register_form.get("repassword");

        if(sex.equals("男")){
            sex = "1";
        }else if(sex.equals("女")){
            sex = "0";
        }
        if(sex.equals("male")){
            sex = "1";
        }else if(sex.equals("female")){
            sex = "0";
        }

        if(username.isEmpty() || sex.isEmpty() ||phone.isEmpty() || code.isEmpty() || password.isEmpty() || repassword.isEmpty()) {
            returnObj.setCode("500");
            return returnObj;
        }
        if(!repassword.equals(password)){
            returnObj.setCode("500");
            return returnObj;
        }
        // 查询是否已经存在用户
        User user = userService.findUserByPhone(phone);
        if(user!=null){
            returnObj.setCode("500");
            returnObj.setMsg("user exist");
            return returnObj;
        }
        // 现在进行验证验证码
        int check_flag = userService.check_phoneCode(phone,code);
        if(check_flag==0){
            returnObj.setMsg("code error");
            returnObj.setCode("500");
            return returnObj;
        } else if (check_flag==-1) {
            returnObj.setMsg("code time out");
            returnObj.setCode("500");
            return returnObj;
        }

        // 插入用户
        int b = userService.addUser(username, sex, age, phone, code, password);
        if(b==1){
            returnObj.setCode("200");
            returnObj.setMsg("success");
        }else{
            returnObj.setCode("500");
            returnObj.setMsg("register failed");
        }
        return returnObj;
    }



    @PostMapping("/get_phone_code")
    public ReturnObj get_phone_code(@RequestBody Map<String,String> register_form){
        ReturnObj returnObj = new ReturnObj();
        String phone = register_form.get("phone");
        if(phone==null || phone.isEmpty()){
            returnObj.setCode(Constants.CODE_400);
            returnObj.setMsg("error");
            return returnObj;
        }
        // 检查用户是否存在
        User user = userService.findUserByPhone(phone);
        if(user!=null){
            returnObj.setCode("500");
            returnObj.setMsg("user exist");
            return returnObj;
        }

        // 用户不存在就可以发送验证码
        boolean b = userService.sendPhoneCode(phone);
        if(b){
            returnObj.setCode("200");
            returnObj.setMsg("success");
        }else{
            returnObj.setCode("500");
            returnObj.setMsg("failed");
        }
        System.out.println("====注册");
        System.out.println(returnObj);
        return returnObj;
    }

    @PostMapping("/get_reset_phone_code")
    public ReturnObj get_reset_phone_code(@RequestBody Map<String,String> register_form){
        ReturnObj returnObj = new ReturnObj();
        String phone = register_form.get("account");
        if(phone==null || phone.isEmpty()){
            returnObj.setCode(Constants.CODE_400);
            returnObj.setMsg("error");
            return returnObj;
        }
        // 检查用户是否存在
        User user = userService.findUserByPhone(phone);
        if(user==null){
            returnObj.setCode("500");
            returnObj.setMsg("user not exist");
            return returnObj;
        }

        // 用户存在就可以发送验证码
        boolean b = userService.sendPhoneCode(phone);
        if(b){
            returnObj.setCode("200");
            returnObj.setMsg("success");
        }else{
            returnObj.setCode("500");
            returnObj.setMsg("failed");
        }
        System.out.println("====重设密码");
        System.out.println(returnObj);
        return returnObj;
    }

    @GetMapping("/profile")
    public ReturnObj profile(@RequestParam(value = "user_id",required = false) String user_id,@RequestParam(value = "phone",required = false)String phone){
        ReturnObj returnObj = new ReturnObj();
        User user = null;
        if(user_id.isEmpty() && !phone.isEmpty()){
            //使用手机号
            user = userService.getUserInfoByPhone(phone);
        }else if(!user_id.isEmpty() && phone.isEmpty()){
            // 使用user ID
            user = userService.getUserInfoByUserId(user_id);
        }else if(!user_id.isEmpty() && !phone.isEmpty()){
            // 就是用user_id
            user = userService.getUserInfoByUserId(user_id);
        }else{
            // 都是空
            returnObj.setCode("500");
            returnObj.setMsg("参数错误");
        }
        if(user==null){
            returnObj.setCode("500");
            returnObj.setMsg("get information error");
        }else{
            returnObj.setCode("200");
            returnObj.setMsg("get information success");
            returnObj.setData(user);
        }
        return returnObj;
    }

    @PostMapping("/modifyProfile")
    public ReturnObj modifyProfile(@RequestBody Map<String,String> user_info){
        ReturnObj returnObj = new ReturnObj();
        int flag = userService.modifyProfile(user_info);
        if(flag==-1){
            returnObj.setCode(Constants.CODE_400);
            returnObj.setMsg("params error");
        } else if (flag == 0) {
            returnObj.setCode(Constants.CODE_500);
            returnObj.setMsg("update failed");
        }else{
            returnObj.setCode(Constants.CODE_200);
            returnObj.setMsg("params success");
        }
        return returnObj;
    }

    @PostMapping("/resetPassword")
    public ReturnObj resetPassword(@RequestBody Map<String,String> reset_form){
        ReturnObj returnObj = new ReturnObj();
        String account = reset_form.get("account");
        String code = reset_form.get("code");
        String password = reset_form.get("password");
        String repassword = reset_form.get("repassword");
        if(account==null || password==null || repassword==null || code==null){
            returnObj.setCode(Constants.CODE_400);
            returnObj.setMsg("参数错误");
            return returnObj;
        }
        //对比密码
        if(!repassword.equals(password)){
            returnObj.setCode(Constants.CODE_400);
            returnObj.setMsg("密码不一致");
            return returnObj;
        }
        // 检查用户是否存在
        User user = userService.findUserByPhone(account);
        if(user==null){
            returnObj.setCode(Constants.CODE_600);
            returnObj.setMsg("user not exist");
            return returnObj;
        }
        // 检验验证码
        int check_flag = userService.check_phoneCode(account,code);
        if(check_flag==0){
            returnObj.setMsg("code error");
            returnObj.setCode(Constants.CODE_400);
            return returnObj;
        } else if (check_flag==-1) {
            returnObj.setMsg("code time out");
            returnObj.setCode(Constants.CODE_400);
            return returnObj;
        }

        // 更新密码
        int reset_flag = userService.resetPassword(account,password);
        if(reset_flag==1){
            returnObj.setCode(Constants.CODE_200);
            returnObj.setMsg("修改成功");
        }else {
            returnObj.setCode(Constants.CODE_500);
            returnObj.setMsg("修改失败，请联系管理员");
        }
        return returnObj;
    }

    //添加收获地址
    @PostMapping("/addaddress")
    public ReturnObj addaddress(@RequestBody Map<String,String> address_form){
        ReturnObj returnObj = new ReturnObj();

        String user_id = address_form.get("user_id");
        String address = address_form.get("address");
        if(user_id==null || address==null){
            returnObj.setCode(Constants.CODE_400);
            returnObj.setMsg("参数错误");
        }
        // 根据user_id进行insertaddress
        int flag = userService.addAddress(user_id,address);
        if(flag==1){
            returnObj.setCode(Constants.CODE_200);
            returnObj.setMsg("success");
        }else{
            returnObj.setCode(Constants.CODE_500);
            returnObj.setMsg("failed");
        }
        return returnObj;
    }

    // 更新地址
    @PostMapping("/updateaddress")
    public ReturnObj updateaddress(@RequestBody Map<String,String> address_form){
        ReturnObj returnObj = new ReturnObj();
        String user_id = address_form.get("user_id");
        String address = address_form.get("address");
        if(user_id==null || address==null){
            returnObj.setCode(Constants.CODE_400);
            returnObj.setMsg("参数错误");
        }
        int flag = userService.updateAddress(user_id,address);
        if(flag==1){
            returnObj.setCode(Constants.CODE_200);
            returnObj.setMsg("success");
        }else{
            returnObj.setCode(Constants.CODE_500);
            returnObj.setMsg("failed");
        }
        return returnObj;
    }

    // 返回地址列表
    @GetMapping("/addresslist")
    public ReturnObj addresslist(@RequestParam(value = "user_id") String user_id){
        ReturnObj returnObj = new ReturnObj();
        try{
            List<Address> addressList = userService.getAddressListByUserId(user_id);
            returnObj.setData(addressList);
            returnObj.setCode(Constants.CODE_200);
            returnObj.setMsg("success");
        }catch (Exception e){
            System.out.printf("返回地址列表");
            System.out.println(e);
            returnObj.setCode(Constants.CODE_500);
            returnObj.setMsg("failed");
        }
        return returnObj;
    }

    // 上传地址id和用户id进行删除
    @DeleteMapping("/deleteAddress")
    public ReturnObj deleteAddress(@RequestParam(value = "user_id") String user_id,@RequestParam(value = "address_id") String address_id){
        ReturnObj returnObj = new ReturnObj();
        int flag = userService.delete_address(user_id,address_id);
        if(flag==1){
            returnObj.setCode(Constants.CODE_200);
            returnObj.setMsg("success");
        }else{
            returnObj.setCode(Constants.CODE_500);
            returnObj.setMsg("failed");
        }
        return returnObj;
    }

    // 头像更新
    @PostMapping("/uploadAvatar")
    public ReturnObj uploadAvatar(MultipartFile image,@RequestParam("user_id") String userId){
        ReturnObj returnObj = new ReturnObj();
        // 创建一个RestTemplate对象
        RestTemplate restTemplate = new RestTemplate();

        // 检查是否接收到文件
        if (image.isEmpty()) {
            returnObj.setCode(Constants.CODE_400);
            returnObj.setMsg("No file received.");
            return returnObj;
        }
        // 检查文件类型
        String contentType = image.getContentType();
        if (!contentType.startsWith("image/")) {
            returnObj.setCode(Constants.CODE_400);
            returnObj.setMsg("Invalid file type. Only image files are allowed.");
            return returnObj;
        }
        String uploadUrl = "http://124.70.51.6:8000/upload/";
        String staticImagePath = "E:\\作业文件\\实训\\code\\petShopUser\\src\\main\\resources\\static\\";
        //保存文件
        String filename = userService.save_avatar(userId,image);
        if(filename==null){
            returnObj.setCode(Constants.CODE_500);
            returnObj.setMsg("failed");
            return returnObj;
        }
//
//        // 设置请求头，指定Content-Type为multipart/form-data
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        String imagePath = staticImagePath+filename;
        String code = sendImageToDjango(imagePath, uploadUrl);

        if(code.equals(Constants.CODE_200)){
            returnObj.setCode(Constants.CODE_200);
            returnObj.setMsg("success");
        }else{
            returnObj.setCode(Constants.CODE_500);
            returnObj.setMsg("failed");
        }

        return returnObj;
    }
    @GetMapping("/getIP")
    public  ReturnObj getIP(@RequestParam String user_id,HttpServletRequest request){
        System.out.println(request);
        ReturnObj returnObj =new ReturnObj();
        Map<String,String> map = new HashMap<>();
        String res =  IpUtil.getIpAddr(request);
        StringBuilder result= new StringBuilder();
        int num=0;
        for(int i=0;i<res.length();i++){
            if(res.charAt(i)=='|'){
                num++;
                if(num==4)
                    break;
                continue;
            }
            if(res.charAt(i)!='0')
                result.append(res.charAt(i));
        }
        if(!result.toString().equals("unknown")){
            userService.setIP(String.valueOf(snowflakeIdWorker.nextId()), user_id,
                    request.getHeader("X-Real-IP"), result.toString());
            map.put("ip", request.getHeader("X-Real-IP"));
            map.put("res", result.toString());
            returnObj.setData(map);
            returnObj.setMsg("success");
            returnObj.setCode("200");
        }
        else{
            returnObj.setData(false);
            returnObj.setMsg("error");
            returnObj.setCode("500");
        }
        return  returnObj;
    }
    @PostMapping("/getIPAddressByUId")
    public ReturnObj getIPAddressByUId(@RequestBody Map<String,String> request_form){
        ReturnObj returnObj =new ReturnObj();
        Ip_address ip_address= userService.getIP(request_form.get("user_id"));
        if(ip_address!=null){
            returnObj.setCode("200");
            returnObj.setMsg("success");
            returnObj.setData(ip_address);
        }
        else{
            returnObj.setCode("500");
            returnObj.setMsg("账号有误或不存在该位置信息");
            returnObj.setData(false);
        }
        return returnObj;
    }

    public String sendImageToDjango(String filePath, String uploadUrl){
        RestTemplate restTemplate = new RestTemplate();

        // Configure the RestTemplate with a ResourceHttpMessageConverter
        restTemplate.getMessageConverters().add(new ResourceHttpMessageConverter());

        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("file", new FileSystemResource(new File(filePath)));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(uploadUrl, requestEntity, String.class);
        System.out.println(responseEntity.toString());
        // Handle the response
        int statusCode = responseEntity.getStatusCodeValue();
        String responseBody = responseEntity.getBody();
        System.out.println("Status Code: " + statusCode);
        System.out.println("Response Body: " + responseBody);
        return responseBody;
    }




}
