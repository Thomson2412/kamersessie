<?php

	$CHECKTIME = 5 * 60;
	$EXPIRETIME = 2 * 60 * 60;
	#Connect to Database 
	$con = mysqli_connect("localhost","root","", "kamer_sessie");
	
	 
	#Check connection 
	if (mysqli_connect_errno()) { 
		echo 'Database connection error: ' . mysqli_connect_error(); 
		exit(); 
	}
	
	while(true) {
		$time = time() - $EXPIRETIME;
		//$time = time() - 60;
		echo $time."\n";
		
		$userdetails = mysqli_query($con, "SELECT * FROM activesession WHERE startTime <= '$time'");
		#If no data was returned, check for any SQL errors 
		if (!$userdetails) { 
			echo 'Could not run select query: ' . mysqli_error($con); 
			exit; 
		} 
		else{
			$response["sessions"] = array();
			if (mysqli_num_rows($userdetails) > 0) {
				// looping through all results			
				$sessions["sessions"] = array();
				
				while ($row = mysqli_fetch_array($userdetails)) {
					// temp user array
					$sessions = array();
					$sessions["sessionname"] = $row["sessionname"];
					$sessions["sessionId"] = $row["sessionId"];

					// push single product into final response array
					array_push($response["sessions"], $sessions);
				}
				echo "Session ". $sessions["sessionname"] . " id: " . $sessions["sessionId"] . " overtime. Going for session destroy \n";

				$userdetails = mysqli_query($con, "DELETE FROM activesession WHERE startTime <= '$time'");
					#If no data was returned, check for any SQL errors 
				if (!$userdetails) { 
					echo 'Could not run delete query: ' . mysqli_error($con); 
					exit; 
				} 
				else{
					echo "Query delete ok \n";
				}
			}
			echo "No sessions overtime \n";
		}		
		sleep($CHECKTIME);
		echo "after sleep $CHECKTIME seconds\n";
	}
?>