<?php 

    #Ensure that the client has provided a value for "username" 
    if (isset($_POST["partyId"]) && $_POST["partyId"] != ""){ 
         
        #Setup variables 
        $partyId = $_POST["partyId"]; 
         
        #Connect to Database 
        $con = mysqli_connect("localhost","root","", "kamer_sessie");
		
         
        #Check connection 
        if (mysqli_connect_errno()) { 
            echo 'Database connection error: ' . mysqli_connect_error(); 
            exit(); 
        } 

        #Escape special characters to avoid SQL injection attacks 
        $partyId = mysqli_real_escape_string($con, $partyId); 
         
        #Query the database to get the user details. 
        $userdetails = mysqli_query($con, "SELECT * FROM user, userhasparty WHERE user.userId = userhasparty.userId AND partyId = '$partyId'"); 

        #If no data was returned, check for any SQL errors 
        if (!$userdetails) { 
            echo 'Could not run query: ' . mysqli_error($con); 
            exit; 
        } 

		$response["users"] = array();
		if (mysqli_num_rows($userdetails) > 0) {
			// looping through all results			
			$users["users"] = array();
			
			while ($row = mysqli_fetch_array($userdetails)) {
				// temp user array
				$users = array();
				$users["userId"] = $row["userId"];
				$users["username"] = $row["username"];
				
				// push single product into final response array
				array_push($response["users"], $users);
			}
		}

        #Output the JSON data 
        echo json_encode($response);  
    }else{ 
        echo "Could not complete query. Missing parameter";  
    } 
?>