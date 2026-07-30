package com.runner.grace.result;

public enum ResponseStatusEnum {
    SUCCESS(200, true, "操作成功！"),
    FAILED(500, false, "操作失败！"),

    // ========== 用户相关 50x ==========
    UN_LOGIN(501, false, "请登录后再继续操作！"),
    TICKET_INVALID(502, false, "会话失效，请重新登录！"),
    NO_AUTH(503, false, "您的权限不足，无法继续操作！"),
    MOBILE_ERROR(504, false, "短信发送失败，请稍后重试！"),
    SMS_NEED_WAIT_ERROR(505, false, "短信发送太快啦~请稍后再试！"),
    SMS_CODE_ERROR(506, false, "验证码过期或不匹配，请稍后再试！"),
    USER_FROZEN(507, false, "用户已被冻结，请联系管理员！"),
    USER_UPDATE_ERROR(508, false, "用户信息更新失败，请联系管理员！"),
    USER_INACTIVE_ERROR(509, false, "请前往[账号设置]修改信息激活后再进行后续操作！"),
    USER_NOT_EXIST_ERROR(516, false, "用户不存在！"),
    MOBILE_ALREADY_EXISTS(517, false, "手机号已注册！"),
    RUNNER_NOT_APPROVED(518, false, "跑腿员账号审核中或已被禁用！"),
    INSUFFICIENT_BALANCE(519, false, "余额不足！"),
    USER_STATUS_ERROR(520, false, "用户状态参数出错！"),

    // ========== 任务相关 52x ==========
    TASK_NOT_FOUND(521, false, "任务不存在！"),
    TASK_ALREADY_TAKEN(522, false, "任务已被接单！"),
    TASK_STATUS_ERROR(523, false, "任务状态错误！"),
    TASK_PUBLISH_ERROR(524, false, "任务发布失败，请重试！"),

    // ========== 文件相关 53x ==========
    FILE_UPLOAD_NULL_ERROR(530, false, "文件不能为空，请选择一个文件再上传！"),
    FILE_UPLOAD_FAILD(531, false, "文件上传失败！"),
    FILE_FORMATTER_FAILD(532, false, "文件图片格式不支持！"),
    FILE_MAX_SIZE_ERROR(533, false, "仅支持500kb大小以下的图片上传！"),
    FILE_NOT_EXIST_ERROR(534, false, "你所查看的文件不存在！"),

    // ========== 管理员相关 56x ==========
    ADMIN_USERNAME_NULL_ERROR(561, false, "管理员登录名不能为空！"),
    ADMIN_USERNAME_EXIST_ERROR(562, false, "管理员登录名已存在！"),
    ADMIN_NAME_NULL_ERROR(563, false, "管理员负责人不能为空！"),
    ADMIN_PASSWORD_ERROR(564, false, "密码不能为空或两次输入不一致！"),
    ADMIN_CREATE_ERROR(565, false, "添加管理员失败！"),
    ADMIN_PASSWORD_NULL_ERROR(566, false, "密码不能为空！"),
    ADMIN_NOT_EXIT_ERROR(567, false, "管理员不存在或密码错误！"),
    ADMIN_FACE_NULL_ERROR(568, false, "人脸信息不能为空！"),
    ADMIN_FACE_LOGIN_ERROR(569, false, "人脸识别失败，请重试！"),

    // ========== 分类相关 57x ==========
    CATEGORY_EXIST_ERROR(570, false, "文章分类已存在，请换一个分类名！"),

    // ========== 人脸识别 60x ==========
    FACE_VERIFY_TYPE_ERROR(600, false, "人脸比对验证类型不正确！"),
    FACE_VERIFY_LOGIN_ERROR(601, false, "人脸登录失败！"),

    // ========== 系统错误 55x ==========
    SYSTEM_ERROR(555, false, "系统繁忙，请稍后再试！"),
    SYSTEM_OPERATION_ERROR(556, false, "操作失败，请重试或联系管理员"),
    SYSTEM_RESPONSE_NO_INFO(557, false, ""),
    SYSTEM_INDEX_OUT_OF_BOUNDS(541, false, "系统错误，数组越界！"),
    SYSTEM_ARITHMETIC_BY_ZERO(542, false, "系统错误，无法除零！"),
    SYSTEM_NULL_POINTER(543, false, "系统错误，空指针！"),
    SYSTEM_NUMBER_FORMAT(544, false, "系统错误，数字转换异常！"),
    SYSTEM_PARSE(545, false, "系统错误，解析异常！"),
    SYSTEM_IO(546, false, "系统错误，IO输入输出异常！"),
    SYSTEM_FILE_NOT_FOUND(547, false, "系统错误，文件未找到！"),
    SYSTEM_CLASS_CAST(548, false, "系统错误，类型强制转换错误！"),
    SYSTEM_PARSER_ERROR(549, false, "系统错误，解析出错！"),
    SYSTEM_DATE_PARSER_ERROR(550, false, "系统错误，日期解析出错！");

    private Integer status;
    private Boolean success;
    private String msg;

    ResponseStatusEnum(Integer status, Boolean success, String msg) {
        this.status = status;
        this.success = success;
        this.msg = msg;
    }

    public Integer status() {
        return status;
    }

    public Boolean success() {
        return success;
    }

    public String msg() {
        return msg;
    }
}