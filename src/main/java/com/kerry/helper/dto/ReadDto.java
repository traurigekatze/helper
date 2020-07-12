package com.kerry.helper.dto;

import com.kerry.helper.annotation.ExcelReadField;
import lombok.Data;

/**
 * **********书山有路勤为径**********
 *
 * @author k1rry
 * @date 2020/7/11
 * **********学海无涯苦作舟**********
 */
@Data
public class ReadDto {

    private Integer id;

    private String name;

    @ExcelReadField("A")
    private String a;

    @ExcelReadField("B")
    private String b;

    @ExcelReadField("C")
    private String c;

    @ExcelReadField("D")
    private String d;

    @ExcelReadField("E")
    private String e;

    @ExcelReadField("F")
    private String f;

    @ExcelReadField("G")
    private String g;

    @ExcelReadField("H")
    private String h;

    @ExcelReadField("I")
    private String i;

    @ExcelReadField("J")
    private String j;

}
