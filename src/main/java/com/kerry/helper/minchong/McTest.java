package com.kerry.helper.minchong;

import com.alibaba.fastjson.JSONObject;
import com.kerry.helper.util.ExcelUtils;
import com.kerry.helper.util.HttpUtils;
import com.kerry.helper.util.Md5Util;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.poi.hssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * **********书山有路勤为径**********
 *
 * @author k1rry
 * @date 2020/7/16
 * **********学海无涯苦作舟**********
 */
public class McTest {

    private static Logger logger = LoggerFactory.getLogger(McTest.class);

    private static final String hashcode = "710157bdb7444cdc9b21e081fc897ae3";

    private static final String password = "HMcFdt";

    private static final String url = "http://106.15.94.46:81/ws/verification/certIDverify.asmx/idverifyA";

    public static void main(String[] args) throws Exception {
        List<McReq> reqList = ExcelUtils.readExcel("C:\\Users\\k1rry\\Desktop\\0719-3.xlsx", McReq.class);
        if (CollectionUtils.isEmpty(reqList)) {
            return;
        }
        List<McResp> list = new ArrayList<>();
        for (McReq req : reqList) {
            req.setIdNo(req.getIdNo().replace("#", "").trim());
            req.setName(req.getName().replace("#", "").trim());
            McResp resp = request(req);
            logger.info("resp:{}", resp);
            list.add(resp);
            TimeUnit.MILLISECONDS.sleep(200);
        }
        export(list);
    }

    private static McResp request(McReq req) throws Exception {
        List<NameValuePair> params = new ArrayList<>();
        McResp resp = new McResp();
        resp.setIdNo(req.getIdNo());
        resp.setName(req.getName());
        resp.setHistory(req.getHistory());

        NameValuePair hashcodeP = new BasicNameValuePair("hashcode", hashcode);
        NameValuePair passNameP = new BasicNameValuePair("passName", req.getName());
        NameValuePair pidP = new BasicNameValuePair("pid", req.getIdNo());

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String ori = hashcode + req.getName() + req.getIdNo() + password + format.format(new Date());
        String sign = Md5Util.encryption(ori);

        NameValuePair signP = new BasicNameValuePair("sign", sign);
        params.add(hashcodeP);
        params.add(passNameP);
        params.add(pidP);
        params.add(signP);
        logger.info("reqJson:{}", params);
        long startTime = System.currentTimeMillis();
        String resJson = HttpUtils.post(url, params);
//        String resJson = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
//                "<PSG_certIDverify xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://xuanyunxinxi.com/\">\n" +
//                "  <ErrorRes>\n" +
//                "    <Err_code>200</Err_code>\n" +
//                "    <Err_content>身份证与姓名一致</Err_content>\n" +
//                "  </ErrorRes>\n" +
//                "</PSG_certIDverify>";
        resp.setUpstreamTime(System.currentTimeMillis() - startTime);
        logger.info("resJson:{}", resJson);
        if (StringUtils.isNotBlank(resJson)) {
            String code = resJson.substring(resJson.indexOf("<Err_code>") + "<Err_code>".length(), resJson.lastIndexOf("</Err_code>"));
            String content = resJson.substring(resJson.indexOf("<Err_content>") + "<Err_content>".length(), resJson.lastIndexOf("</Err_content>"));
            resp.setResult(code + "-" + content);
        } else {
            resp.setResult(resJson);
        }
        return resp;
    }

    private static void export(List<McResp> list) throws IOException {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        // Create a row and put some cells in it. Rows are 0 based.
        HSSFRow row = sheet.createRow((short)0);
        // Create a cell and put a value in it.
        HSSFCell c0 = row.createCell((short)0);
        HSSFCell c1 = row.createCell((short)1);
        HSSFCell c2 = row.createCell((short)2);
        HSSFCell c3 = row.createCell((short)3);
        HSSFCell c4 = row.createCell((short)4);
        c0.setCellValue("idNo");
        c1.setCellValue("name");
        c2.setCellValue("history");
        c3.setCellValue("result");
        c4.setCellValue("upstreamTime");
        for (int i = 0; i < list.size(); i++) {
            HSSFRow r = sheet.createRow((short)i+1);

            HSSFCell rc0 = r.createCell((short)0);
            HSSFCell rc1 = r.createCell((short)1);
            HSSFCell rc2 = r.createCell((short)2);
            HSSFCell rc3 = r.createCell((short)3);
            HSSFCell rc4 = r.createCell((short)4);

            rc0.setCellValue(list.get(i).getIdNo());
            rc1.setCellValue(list.get(i).getName());
            rc2.setCellValue(list.get(i).getHistory());
            rc3.setCellValue(list.get(i).getResult());
            rc4.setCellValue(list.get(i).getUpstreamTime());
        }
        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream("C:\\Users\\k1rry\\Desktop\\0719-3-" + UUID.randomUUID() + "-result.xls");
        wb.write(fileOut);
        fileOut.close();
        logger.info("excel export ok");
    }

}
