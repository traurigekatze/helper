package com.kerry.helper.minchong;

import com.kerry.helper.annotation.ExcelReadField;
import lombok.Data;

/**
 * **********书山有路勤为径**********
 *
 * @author k1rry
 * @date 2020/7/16
 * **********学海无涯苦作舟**********
 */
@Data
public class McReq {

    @ExcelReadField("idNo")
    private String idNo;

    @ExcelReadField("name")
    private String name;

    @ExcelReadField("history")
    private String history;

}
