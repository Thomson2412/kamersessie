<?php 

    #Ensure that the client has provided a value for "username" 
    if (isset($_POST["userId"]) && $_POST["userId"] != ""
			&& isset($_POST["partyId"]) && $_POST["partyId"] != ""){ 
         
        #Setup variables 
        $userId = $_POST["userId"];
		$partyId = $_POST["partyId"];
         
        #Connect to Database 
        $con = mysqli_connect("localhost","root","", "kamer_sessie"); 
         
        #Check connection 
        if (mysqli_connect_errno()) { 
            echo 'Database connection error: ' . mysqli_connect_error(); 
            exit(); 
        } 

        #Escape special characters to avoid SQL injection attacks 
        $userId = mysqli_real_escape_string($con, $userId);
		$partyId = mysqli_real_escape_string($con, $partyId);
         
        #Query the database to get the user details. 
        $userdetails = mysqli_query($con, "SELECT * FROM userhasparty WHERE userId = '$userId' AND partyId = '$partyId'"); 

        #If no data was returned, check for any SQL errors 
        if (!$userdetails) { 
            echo 'Could not run query: ' . mysqli_error($con); 
            exit; 
        } 

        #Get the first row of the results 
        $row = mysqli_fetch_row($userdetails);
		if(mysqli_num_rows($userdetails) == 0){
			$userdetails = mysqli_query($con, "INSERT INTO userhasparty (uhpid, userId, partyId) VALUES (NULL, '$userId', '$partyId')");
			if (!$userdetails) {
				echo 'Could not run query: ' . mysqli_error($con); 
				exit; 
			}else { 
				$result_data = array(
				'empty' => true,
				'added' => true);
				echo json_encode($result_data);
			}			
		} else{
			#Output the JSON data 
			$result_data = array(
				'empty' => false,
				'added' => false);
			echo json_encode($result_data);  
		}
    }else{ 
        echo "Could not complete query. Missing parameter";  
    } 
?>