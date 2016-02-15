<?php 

	$EXPIRETIME = 2 * 60 * 60;
    #Ensure that the client has provided a value for "username" 
    if (isset($_POST["userId"]) && $_POST["userId"] != ""){ 
         
        #Setup variables 
        $userId = $_POST["userId"]; 
         
        #Connect to Database 
        $con = mysqli_connect("localhost","root","", "kamer_sessie");
		
         
        #Check connection 
        if (mysqli_connect_errno()) { 
            echo 'Database connection error: ' . mysqli_connect_error(); 
            exit(); 
        } 

        #Escape special characters to avoid SQL injection attacks 
        $userId = mysqli_real_escape_string($con, $userId); 
        $time = time() - $EXPIRETIME; 
        #Query the database to get the user details. 
        $userdetails = mysqli_query($con, "SELECT * FROM user, userhasparty, party, partyhasactivesession, activesession WHERE user.userId = activesession.createdByUserId AND party.partyId = userhasparty.partyid AND userhasparty.partyId = partyhasactivesession.partyId AND activesession.sessionId = partyhasactivesession.sessionId AND userhasparty.userId = '$userId' AND createdByUserId <> '$userId' AND activesession.startTime > '$time'"); 

        #If no data was returned, check for any SQL errors 
        if (!$userdetails) { 
            echo 'Could not run query: ' . mysqli_error($con); 
            exit; 
        } 

		$response["sessions"] = array();
		if (mysqli_num_rows($userdetails) > 0) {
			// looping through all results			
			$sessions["sessions"] = array();
			
			while ($row = mysqli_fetch_array($userdetails)) {
				// temp user array
				$sessions = array();
				$sessions["username"] = $row["username"];
				$sessions["sessionname"] = $row["sessionname"];
				$sessions["location"] = $row["location"];
				$sessions["partyname"] = $row["partyname"];
				$sessions["sessionId"] = $row["sessionId"];
				$sessions["startTime"] = $row["startTime"];
				
				// push single product into final response array
				array_push($response["sessions"], $sessions);
			}
		}

        #Output the JSON data 
        echo json_encode($response);  
    }else{ 
        echo "Could not complete query. Missing parameter";  
    } 
?>