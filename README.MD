<!DOCTYPE html>
<html>
<head>
</head>
<body>
	<h2>Autonomous Agricultural Robot - Mobile Application</h2>
	<h4>Group Members</h4>
		<div>
			<div>Denuke Dissanayake - E/16/089 - e16089@eng.pdn.ac.lk</div>
			<div>Maneesha Randeniya - E/16/313 - e16313@eng.pdn.ac.lk</div>
			<div>Nipun Dewanarayana - E/16/360 - e16360@eng.pdn.ac.lk</div>
		</div>
    <h4>Description</h4>
		<p>
			<div>AgriBot is an automated robot. A mobile app is used to collect the information and displayed a graphical change oftemperature and humidity to the user. Mobile app is used to give start, stop, pause commands to the robot and details about the field, like row length, gap of seeds, gap of rows, how many rows like that.
using those informations robot will go to each point and drill the soil, plant a seed.
      </div><br>
			<div>The main purpose of our Robot is plant seeds over large farm areas!!!!</div><br>
      <div>In the system, robot will connected to the AWS server, inside that robot and app will connect through mqtt broker. Mainly this application sends messages which critical in the system like start, stop, pause signals. Also initial configuration and also connectivity of the device. In the robot side it will receive these and also output sensor data to the broker. Mobile application is display these data. For these tasks we use MQTT architecture and protocol which is the best solution for IOT. This is different from normal http architecture and for that we use java library - paho.mqtt lib for this implementation. Android application made from java (native approach) which is run faster. If you more interested about this App and project, project site attached to this markdown.
	     
project site https://cepdnaclk.github.io/e16-3yp-agribot/
project git  https://github.com/Nipun-Dewenarayane/e16-3yp-agribot	      
      </div>
		</p>
	<br>
	<h4>Mobile App Screen Shots</h4>
	<br>
	<table width="100%" border="0">
		<tr>
			<td><img src="ui1.JPG" align="left"></td>
			<td><img src="ui2.JPG" align="center"></td>
    			<td><img src="ui3.JPG" align="right"></td>
		</tr>
		<tr>
			<td><img src="ui4.JPG" align="left"></td>
			<td><img src="ui5.JPG" align="center"></td>
		</tr>
	</table>
	
   <h4>Advisers</h4>
    <div> Dr Isuru Nawinna </div>
    <div> Dr Ziyan Marrikkar </div>
		<h4>Links :</h4>
			<div>
				<div><a href="https://www.pdn.ac.lk/academics/academics.php/ "> University of Peradeniya</a> </div>
				<div><a href="http://eng.pdn.ac.lk/"> Faculty of Engineering</a></div>
        <div><a href="http://www.ce.pdn.ac.lk/"> Department of Computer Engineering </a> </div>
			</div>
	
	
</body>
</html>
