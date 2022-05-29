package org.test.imagereplace;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import org.jeff.Application;
import org.jeff.mapper.CarouselMapper;
import org.jeff.mapper.ItemsImgMapper;
import org.jeff.mapper.OrderItemsMapper;
import org.jeff.mapper.UsersMapper;
import org.jeff.pojo.ItemsImg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库图片替换
 *
 * @author jeff
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ImageReplace {

    @Resource
    private UsersMapper usersMapper;

    @Resource
    private ItemsImgMapper itemsImgMapper;

    @Resource
    private OrderItemsMapper orderItemsMapper;

    @Resource
    private CarouselMapper carouselMapper;
    @Resource
    private RestTemplate restTemplate;

    @Test
    public void replace() {

        //List<Carousel> carousels = carouselMapper.selectAll();
        Map<String, Object> map = new HashMap<>();
        //System.out.println(carousels);

        //List<Users> users = usersMapper.selectAll();
        //String facePath = "./face";
        //users.forEach(user ->{
        //    String imageUrl = user.getFace();
        //    DownloadResource(imageUrl,facePath,null);
        //    String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        //    String newUrl = upload(new File(facePath + File.separator+ fileName));
        //    Users newUser = new Users();
        //    newUser.setId(user.getId());
        //    newUser.setFace(newUrl);
        //    usersMapper.updateByPrimaryKeySelective(newUser);
        //});

        List<ItemsImg> itemsImgs = itemsImgMapper.selectAll();
        String itemsImgPath = "./itemImage";
        Object[] objects = itemsImgs.toArray();
        for (int i = 0; i < 30; i++) {
            ItemsImg item = (ItemsImg)objects[i];

        }
        for (ItemsImg itemsImg : itemsImgs) {
            String imageUrl = itemsImg.getUrl();
            if (imageUrl.contains("http://122.152.205.72:88/")) {
                DownloadResource(imageUrl, itemsImgPath, null);
                String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                String newUrl = "";

                if (map.get(imageUrl) == null) {
                    newUrl = upload(new File(itemsImgPath + File.separator + fileName));
                }else {
                    newUrl = String.valueOf(map.get(imageUrl));
                }
                if (!StringUtils.isEmpty(newUrl) && !"null".equals(newUrl)) {
                    map.put(imageUrl, newUrl);
                    ItemsImg t = new ItemsImg();
                    t.setId(itemsImg.getId());
                    t.setUrl(newUrl);
                    itemsImgMapper.updateByPrimaryKeySelective(t);
                }
            }
        }


        //String imageUrl = upload(new File("./carousels/wallhaven-y8lqo7.jpg"));
        //System.out.println(imageUrl);
        //
        //carousels.forEach(item -> {
        //    String imageUrl = item.getImageUrl();
        //    DownloadResource(imageUrl,"./carousels",null);
        //
        //    File file = new File("./carousels/" + imageUrl.substring(imageUrl.lastIndexOf("/") + 1));
        //    HttpResponse result1 =  HttpRequest.post("https://imgtu.com/json")
        //            .header("cookie","_ga=GA1.2.1980451651.1652777493; _gid=GA1.2.142651584.1653029790; PHPSESSID=dsps2un2f89bu1p996a1vet68d; Hm_lvt_55b3353e0477d9e68e0af53ac08c3c42=1652777493,1653029790,1653054381; Hm_lpvt_55b3353e0477d9e68e0af53ac08c3c42=1653054513")
        //            .form("type", "file")
        //            .form("action", "upload")
        //            .form("auth_token", "e284a4889b29566232e73bfc221ea3d1be05244d")
        //            .form("source", file)
        //            .execute();
        //
        //    JSONObject jsonObject = new JSONObject(result1.body());
        //    Object fileUrl = new JSONObject(jsonObject.get("image")).get("url");
        //    System.out.println(fileUrl);
        //    map.put(imageUrl,String.valueOf(fileUrl));
        //});

    }

    public static String upload(File file) {
        HttpResponse result1 = HttpRequest.post("https://imgtu.com/json")
                .header("cookie", "_ga=GA1.2.1980451651.1652777493; _gid=GA1.2.142651584.1653029790; PHPSESSID=dsps2un2f89bu1p996a1vet68d; Hm_lvt_55b3353e0477d9e68e0af53ac08c3c42=1652777493,1653029790,1653054381; Hm_lpvt_55b3353e0477d9e68e0af53ac08c3c42=1653054513")
                .form("type", "file")
                .form("action", "upload")
                .form("auth_token", "124a12c7056475879e445cf3df847849dfda95ae")
                .form("source", file)
                .execute();

        JSONObject jsonObject = new JSONObject(result1.body());
        System.out.println("上传错误" + new JSONObject(jsonObject.get("error")).get("message"));
        Object fileUrl = new JSONObject(jsonObject.get("image")).get("url");
        return String.valueOf(fileUrl);
    }

    /**
     * 文件转为二进制字符串
     *
     * @param file
     * @return
     */
    public static String fileToBinStr(File file) {
        try {
            InputStream fis = new FileInputStream(file);
            byte[] bytes = FileCopyUtils.copyToByteArray(fis);
            return new String(bytes, "ISO-8859-1");
        } catch (Exception ex) {
            throw new RuntimeException("transform file into bin String 出错", ex);
        }
    }


    /**
     * 读取、下载对应的 URL 资源
     *
     * @param resourceUrl URL资源路径
     * @param targetPath  输出路径
     */
    public static void DownloadResource(String resourceUrl, String targetPath, String newFileName) {

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            URL url = new URL(resourceUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            if (newFileName == null || "".equals(newFileName)) {
                newFileName = resourceUrl.substring(resourceUrl.lastIndexOf("/") + 1);
            }

            if (targetPath == null || "".equals(targetPath)) {
                targetPath = "./";
            }
            //文件路径(路径+文件名)
            File file = new File(targetPath + File.separator + newFileName);
            if (!file.exists()) {
                //文件不存在则创建文件，先创建目录
                String parent = file.getParent();
                if (!file.getParentFile().exists()) {
                    File dir = new File(parent);
                    dir.mkdirs();
                }
                file.createNewFile();
            }

            // 获取输入流数据
            inputStream = urlConnection.getInputStream();
            outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            System.out.println("download success");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                assert inputStream != null;
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                assert outputStream != null;
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            urlConnection.disconnect();
        }
    }
}
