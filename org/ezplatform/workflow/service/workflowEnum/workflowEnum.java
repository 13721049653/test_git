package org.ezplatform.workflow.service.workflowEnum;

/**
 * Created by apple on 2021-3-10.
 */
public enum workflowEnum {

    YGBX("ygbx", "AP_OtherPayable"), RLXZLBX("rlxzlbx", "AP_OtherPayable"), DGCGLBX("cglbx", "AP_OtherPayable");

    // 成员变量
    private String index;
    private String value;
    // 构造方法
    private workflowEnum(String index, String value) {
        this.value = value;
        this.index = index;
    }

    public static  String getName(String index) {
        for (workflowEnum c : workflowEnum.values()) {
            if (c.index.equals(index)) {
                return c.value;
            }
        }
        return null;
    }


}
