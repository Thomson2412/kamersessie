<?php 

    #Ensure that the client has provided a value for "username" 
    if (isset($_POST["partyId"]) && $_POST["partyId"] != ""
			&& isset($_POST["userId"]) && $_POST["userId"] != ""){ 
         
        #Setup variables 
        $partyId = $_POST["partyId"]; 
		$userId = $_POST["userId"];
		
		$timestamp = time();
		
        #Connect to Database 
        $con = mysqli_connect("localhost","root","", "kamer_sessie"); 
         
        #Check connection 
        if (mysqli_connect_errno()) { 
            echo 'Database connection error: ' . mysqli_connect_error(); 
            exit(); 
        } 

        #Escape special characters to avoid SQL injection attacks 
        $partyId = mysqli_real_escape_string($con, $partyId);
		$userId = mysqli_real_escape_string($con, $userId);		
         
        #Query the database to get the user details. 
        $userdetails = mysqli_query($con, "DELETE FROM party WHERE partyId = '$partyId' AND partyAdmin = '$userId'");
		

        #If no data was returned, check for any SQL errors 
        if (!$userdetails) { 
            echo 'Could not run query: ' . mysqli_error($con); 
            exit; 
        } 

		$result_data = array(
			'result' => $userdetails
			);

        #Output the JSON data 
        echo json_encode($result_data);  
    }else{ 
        echo "Could not complete query. Missing parameter";  
    } 
?>