<?php 

    #Ensure that the client has provided a value for "username" 
    if (isset($_POST["sessionname"]) && $_POST["sessionname"] != ""
			&& isset($_POST["startTime"]) && $_POST["startTime"] != ""
			&& isset($_POST["location"]) && $_POST["location"] != ""
			&& isset($_POST["partyId"]) && $_POST["partyId"] != ""
			&& isset($_POST["userId"]) && $_POST["userId"] != ""){ 
         
        #Setup variables 
		$sessionname = $_POST["sessionname"];
        $startTime = $_POST["startTime"]; 
		$location = $_POST["location"];
		$partyId = $_POST["partyId"];
		$userId = $_POST["userId"];
		
        #Connect to Database 
        $con = mysqli_connect("localhost","root","", "kamer_sessie"); 
         
        #Check connection 
        if (mysqli_connect_errno()) { 
            echo 'Database connection error: ' . mysqli_connect_error(); 
            exit(); 
        } 

        #Escape special characters to avoid SQL injection attacks
		$sessionname = mysqli_real_escape_string($con, $sessionname);		
        $startTime = mysqli_real_escape_string($con, $startTime);
		$location = mysqli_real_escape_string($con, $location);
		$partyId = mysqli_real_escape_string($con, $partyId);
		$userId = mysqli_real_escape_string($con, $userId);		
         
        #Query the database to get the user details. 
        $userdetails = mysqli_query($con, "INSERT INTO activesession (sessionId, sessionname, startTime, location, createdByUserId) VALUES (NULL, '$sessionname', '$startTime', '$location', '$userId')");
		if($userdetails){
			$row = mysqli_fetch_row(mysqli_query($con, "SELECT * FROM activesession WHERE startTime = '$startTime'"));
			$userdetails = mysqli_query($con, "INSERT INTO partyhasactivesession (partyhasactivesessionId, partyId, sessionId) VALUES (NULL, '$partyId', '$row[0]')");
		}			

        #If no data was returned, check for any SQL errors 
        if (!$userdetails) { 
            echo 'Query could not run query: ' . mysqli_error($con); 
            exit; 
        } 

		$result_data = array(
			'result' => $userdetails
			);

        #Output the JSON data 
        echo json_encode($result_data);  
    }else{ 
        echo "Parameter could not complete query. Missing parameter";  
    } 
?>