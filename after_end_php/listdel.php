<?php

/**
 * PHP version 7.4.28
 * 
 * @category 彩云待办待办删除接口
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

if (isset($_POST['lid'])) {
    $con = mysqli_connect('127.0.0.1', 'root', 'root'); // 连接数据库
    if (!$con) {
        $result_msg['message'] = "数据库连接失败";
        echo json_encode($result_msg);
        exit();
    }
    mysqli_select_db($con, 'ic_todo_list'); // 选择数据库
    $lid = $_POST['lid'];
    $sql1 = "SELECT lid FROM todo_list WHERE lid = $lid";
    $resule1 = mysqli_query($con, $sql1);
    $row = mysqli_num_rows($resule1);
    if ($row) {
        $sql2 = "DELETE FROM todo_list WHERE lid = '$lid'";
        $result2 = mysqli_query($con, $sql2);
        if ($result2) {
            $result_msg['code'] = '200';
            $result_msg['message'] = '成功';
            mysqli_free_result($result2); // 关闭结果集
        } else {
            $result_msg['message'] = '执行失败';
        }
    } else {
        $result_msg['message'] = '数据不存在';
    }
} else {
    $result_msg['message'] = '参数为空';
}

// 发送数据
echo json_encode($result_msg);
