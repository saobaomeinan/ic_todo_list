<?php

/**
 * PHP version 7.4.28
 * 
 * @category 彩云待办待办修改接口
 * @package  Other/testAPI
 * @author   骚包美男 <saobaomeinan@163.com>
 * @license  none none
 * @link     none
 */
//关闭错误警告
error_reporting(E_ERROR);
ini_set("display_errors", "Off");
//  定义返回数据默认值
$result_msg = array(
    'code' => '400',
    'message' => '错误'
);

if (isset($_POST['lid']) && isset($_POST['title'])) {
    $con = mysqli_connect('127.0.0.1', 'root', 'root'); // 连接数据库
    if (!$con) {
        $result_msg['message'] = "数据库连接失败";
        echo json_encode($result_msg);
        exit();
    }
    mysqli_select_db($con, 'ic_todo_list'); // 选择数据库
    $lid = $_POST['lid'];
    $title = $_POST['title'];
    $time = isset($_POST['time']) && $_POST['time'] != "" ? "'" . $_POST['time'] . "'" : "default";
    $content = isset($_POST['content']) && $_POST['content'] != "" ? "'" . $_POST['content'] . "'" : "default";
    $level = isset($_POST['level']) && $_POST['level'] != "" ? "'" . $_POST['level'] . "'" : "default";
    $valid = isset($_POST['valid']) && $_POST['valid'] != "" ? "'" . $_POST['valid'] . "'" : "default";
    $sql1 = "SELECT lid FROM todo_list WHERE lid = '$lid'";
    $result1 = mysqli_query($con, $sql1);
    $num1 = mysqli_num_rows($result1);
    if ($num1) {
        $sql2 = "UPDATE todo_list SET remind_time=$time,list_title='$title',list_content=$content,list_level=$level,valid=$valid WHERE lid = $lid";
        $result2 = mysqli_query($con, $sql2);
        if ($result2) {
            $result_msg['code'] = '200';
            $result_msg['message'] = '成功';
            mysqli_free_result($result2); // 关闭结果集
        } else {
            $result_msg['message'] = '执行失败';
        }
    } else {
        $result_msg['message'] = '待办不存在';
    }
} else {
    $result_msg['message'] = '参数为空';
}

// 发送数据
echo json_encode($result_msg);
