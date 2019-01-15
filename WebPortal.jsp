<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import = "java.io.*,java.util.*,java.sql.*"%>
<%@ page import = "javax.servlet.http.*,javax.servlet.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix = "c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix = "sql"%>


<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Ambiance Monitoring System</title>
</head>
<body>
	<h2>Sensed Humidity, Temperature and Soil Moisture </h2>
<body>
      <sql:setDataSource var = "snapshot" driver = "org.apache.drill.jdbc.Driver"
         url = "jdbc:drill:zk=localhost:2181/drill/drillbits1"
         user = ""  password = ""/>
		<sql:query dataSource = "${snapshot}" var = "result">
        	SELECT CONVERT_FROM(row_key, 'UTF8') As id, CONVERT_FROM(SensData.ambiance.humidity, 'UTF8') As humidity, CONVERT_FROM(SensData.ambiance.tempc, 'UTF8') As tempc, CONVERT_FROM(SensData.ambiance.tempf, 'UTF8') As tempf, CONVERT_FROM(SensData.soil.moisture, 'UTF8') As moist FROM hbase.SensData
      	</sql:query>
      	
      	<table border = "1" width = "100%">
         <tr>
            <th>row ID</th>
            <th>humidity</th>
            <th>tempc</th>
            <th>tempf</th>
            <th>moistute</th>
         </tr>
         
         <c:forEach var = "row" items = "${result.rows}">
            <tr>
               <td><c:out value = "${row.id}"/></td>
               <td><c:out value = "${row.humidity}"/></td>
               <td><c:out value = "${row.tempc}"/></td>
               <td><c:out value = "${row.tempf}"/></td>
               <td><c:out value = "${row.moist}"/></td>
            </tr>
         </c:forEach>
      </table>
</body>
</html>