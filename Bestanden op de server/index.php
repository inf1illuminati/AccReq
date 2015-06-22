
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <!--
    Modified from the Debian original for Ubuntu
    Last updated: 2014-03-19
    See: https://launchpad.net/bugs/1288690
  -->
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Webserver Default Page: It just works!</title>
  </head>

  <body>
<p>Webserver Default Page: It just works!</p>
<?php
    $dBConnect = @mysql_connect("127.0.0.1", "robotarm", '$_Rbrm2015') or die(mysql_error());
    mysql_select_db("robotarm") or die(mysql_error());
    if (isset($_GET["x"]) && isset($_GET["y"]) && isset($_GET["z"])) {
       
    $query = "INSERT INTO robotarm_Test.waarden (x, y, z, date) VALUES ('{$_GET["x"]}','{$_GET["y"]}', '{$_GET["z"]}', '{$_GET["echo date('Y-m-d H:i:s');"]}')";
    //$query = "Select * from robotarm_Test.waarden";
    mysql_query($query, $dBConnect) or die(mysql_error());
    
//    while($value = mysql_fetch_assoc($result)) {
//        echo $value["Waarde"];
//        echo "hoi";
//    }
    }
    mysql_close();
?>
  </body>
</html>