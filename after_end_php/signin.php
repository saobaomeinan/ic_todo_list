<?php

/**
 * PHP version 7.4.28
 * 
 * @category 彩云待办登录接口
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

if (isset($_POST['username']) && isset($_POST['password'])) {
    $username = $_POST["username"];
    $password = $_POST["password"];
    $con = mysqli_connect("127.0.0.1", "root", "root");
    mysqli_select_db($con, "ic_todo_list");
    $sql1 = "select uid,username,password from user where username = '$_POST[username]' and password = '$_POST[password]'";
    $result1 = mysqli_query($con, $sql1);
    $num1 = mysqli_num_rows($result1);
    if ($num1) {
        $userdate = mysqli_fetch_array($result1, MYSQLI_NUM);  //将数据以索引方式储存在数组中
        $result_msg['code'] = '200';
        $result_msg['message'] = '成功'; // 登录成功
        $result_msg['uid'] = $userdate[0];
        mysqli_free_result($result1); // 关闭结果集
    } else {
        $result_msg['message'] = '用户或密码错误';
    }
    mysqli_close($con); //关闭MySQL服务器
} else {
    $result_msg['message'] = '参数为空';
}

// 发送数据
echo json_encode($result_msg);
