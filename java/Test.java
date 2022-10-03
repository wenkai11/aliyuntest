import Utils.HttpUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @Author :wk
 * @Description
 * @Date: Created in 14:36 2022/9/29
 * @modified By:
 */
public class Test {
    public static void main(String[] args) throws Exception {
        String host = "https://ocrapi-advanced.taobao.com";
        String path = "/ocrservice/advanced";
        String method = "POST";
        String appcode = "你自己的appcode";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/json; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        System.out.println("请输入需要识别的页码");
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int i;
        for(i=1; i<=n;i++) {
            String bodys = "{\"img\":\"" + changeToBase64("D:\\123\\"+i+".png") + "\",\"sortPage\": true}";
            try {
                /**
                 * 重要提示如下:
                 * HttpUtils请从
                 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
                 * 下载
                 *
                 * 相应的依赖请参照
                 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
                 */
                HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
                System.out.println(response.toString());
                //获取response的body
                prase(EntityUtils.toString(response.getEntity()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static void output(String content) {
        System.out.println(content);
        File file = new File("D:\\result.docx");  //这里选择输出文件的地址
        try {
            FileWriter writer = new FileWriter(file, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 解析Json
     * @param string 获取response中的Json
     */
    public static void prase(String string){
        JSONObject jsonObject=JSONObject.parseObject(string);
        JSONArray jsonArray=jsonObject.getJSONArray("prism_wordsInfo");
        for (int i=0;i<jsonArray.size();i++){
            JSONObject newjsonObject=(JSONObject) jsonArray.get(i);
            output(newjsonObject.getString("word"));
        }
    }
    public static String changeToBase64(String fileName) throws Exception{
        File file = new File(fileName);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int)file.length()];
        inputFile.read(buffer);
        inputFile.close();
        String base64Code=new BASE64Encoder().encode(buffer);
        return base64Code;
    }
}
