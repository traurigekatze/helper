package com.kerry.helper.zx;

import com.alibaba.fastjson.JSONObject;
import com.kerry.helper.minchong.McReq;
import com.kerry.helper.minchong.McResp;
import com.kerry.helper.minchong.McTest;
import com.kerry.helper.util.ExcelUtils;
import com.kerry.helper.util.HttpUtils;
import com.kerry.helper.util.Md5Util;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * **********书山有路勤为径**********
 *
 * @author k1rry
 * @date 2020/7/16
 * **********学海无涯苦作舟**********
 */
public class ZxTest {


    private static Logger logger = LoggerFactory.getLogger(McTest.class);

    private static final String password = "cl123456";

    private static final String account = "test0628";

    private static final String service = "001040000";

    private static final String url = "https://service.chinaftg.com:7443/api/v1/app/creditservice";

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
        McResp resp = new McResp();
        resp.setIdNo(req.getIdNo());
        resp.setName(req.getName());
        resp.setHistory(req.getHistory());

        JSONObject data = new JSONObject(true);

        JSONObject meta = new JSONObject();
        meta.put("account", account);
        meta.put("password", password);
        meta.put("service_code", service);

        JSONObject params = new JSONObject();
        params.put("name", req.getName());
        params.put("id_no", req.getIdNo());

        data.put("meta", meta);
        data.put("params", params);

        String reqJson = data.toJSONString();
        logger.info("reqJson:{}", reqJson);
        long startTime = System.currentTimeMillis();
        String resJson = HttpUtils.doPostJson(url, reqJson);
        resp.setUpstreamTime(System.currentTimeMillis() - startTime);
        logger.info("resJson:{}", resJson);
        if (StringUtils.isNotBlank(resJson)) {
            JSONObject object = JSONObject.parseObject(resJson);
            JSONObject data1 = object.getJSONObject("data");
            if (data1 != null) {
                String result = data1.getString("citizen_result");
                resp.setResult(result);
            }
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
