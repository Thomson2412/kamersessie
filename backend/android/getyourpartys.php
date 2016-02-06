<?php 

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
         
        #Query the database to get the user details. 
        $userdetails = mysqli_query($con, "SELECT * FROM party, userhasparty WHERE party.partyId = userhasparty.partyId AND userId = '$userId'"); 

        #If no data was returned, check for any SQL errors 
        if (!$userdetails) { 
            echo 'Could not run query: ' . mysqli_error($con); 
            exit; 
        } 

		$response["partys"] = array();
		if (mysqli_num_rows($userdetails) > 0) {
			// looping through all results			
			$partys["partys"] = array();
			
			while ($row = mysqli_fetch_array($userdetails)) {
				// temp user array
				$partys = array();
				$partys["partyId"] = $row["partyId"];
				$partys["partyname"] = $row["partyname"];
				$partys["partyAdmin"] = $row["partyAdmin"];
				$partys["created"] = $row["created"];
				
				// push single product into final response array
				array_push($response["partys"], $partys);
			}
		}

        #Output the JSON data 
        echo json_encode($response);  
    }else{ 
        echo "Could not complete query. Missing parameter";  
    } 
?>