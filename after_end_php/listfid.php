<?php

/**
 * PHP version 7.4.28
 * 
 * @category 彩云待办待办查询接口
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

if (isset($_POST['uid'])) {
    $uid = $_POST['uid'];
    $con = mysqli_connect("127.0.0.1", "root", "root");
    mysqli_select_db($con, "ic_todo_list");
    $sql1 = "select uid from user where uid = '$uid'";
    $result1 = mysqli_query($con, $sql1);
    $num1 = mysqli_num_rows($result1);
    if ($num1) {
        $userdate = mysqli_fetch_array($result1, MYSQLI_NUM);  //将数据以索引方式储存在数组中
        $sql2 = "select * from todo_list where user_id = '$userdate[0]'";
        $result2 = mysqli_query($con, $sql2);
        $num2 = mysqli_num_rows($result2);
        $result_msg['code'] = '200';
        $result_msg['message'] = '成功'; // 成功
        if ($num2) { // list有值
            $listdatas = array();
            while ($listdata = mysqli_fetch_array($result2, MYSQLI_ASSOC)) {
                $listdatas[] = $listdata;
            }
            $result_msg['data'] = $listdatas;
        } else {
            $result_msg['data'] = 'null';
        }
        mysqli_free_result($result2);
    } else {
        $result_msg['message'] = '用户不存在';
    }
} else {
    $result_msg['message'] = '参数为空';
}

// 发送数据
echo json_encode($result_msg);
