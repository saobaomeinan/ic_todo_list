<?php

/**
 * PHP version 7.4.28
 * 
 * @category 彩云代办注册接口
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

if (isset($_POST['username']) && isset($_POST['password']) && isset($_POST['user_nickname'])) {
    $con = mysqli_connect('127.0.0.1', 'root', 'root'); // 连接数据库
    if (!$con) {
        $result_msg['message'] = "数据库连接失败";
        echo json_encode($result_msg);
        exit();
    }
    mysqli_select_db($con, 'ic_todo_list'); // 选择数据库
    $username = $_POST['username'];
    $password = $_POST['password'];
    $user_nickname = $_POST['user_nickname'];
    $sql1 = "select * from user where username = '$username'";
    $result1 = mysqli_query($con, $sql1);
    $num = mysqli_num_rows($result1);
    if ($num) {
        $result_msg['message'] = '用户存在';
        echo json_encode($result_msg);
        exit();
    }
    $sql2 = "insert into user values(null, '$username', '$password', '$user_nickname', null, null)";
    $result2 = mysqli_query($con, $sql2);
    if ($result2) {
        $result_msg['code'] = '200';
        $result_msg['message'] = '成功';
        mysqli_free_result($result); // 关闭结果集
    } else {
        $result_msg['message'] = '插入数据失败';
    }
    mysqli_close($con); //关闭MySQL服务器
} else {
    $result_msg['message'] = '参数为空';
}

// 发送数据
echo json_encode($result_msg);
