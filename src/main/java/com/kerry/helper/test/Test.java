package com.kerry.helper.test;

import com.alibaba.fastjson.JSON;
import com.kerry.helper.dto.ReadDto;
import com.kerry.helper.util.ExcelUtils;
import java.util.List;

/**
 * **********书山有路勤为径**********
 *
 * @author k1rry
 * @date 2020/7/11
 * **********学海无涯苦作舟**********
 */
public class Test {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        String filePath = "G:\\123\\test-read.xlsx";
        List<ReadDto> list = ExcelUtils.readExcel(filePath, ReadDto.class);
        System.out.println(JSON.toJSONString(list));

        System.out.println("use time:" + (System.currentTimeMillis()-startTime));
    }

}
