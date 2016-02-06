<?php 

    #Ensure that the client has provided a value for "username" 
    if (isset($_POST["partyname"]) && $_POST["partyname"] != ""
			&& isset($_POST["partyAdmin"]) && $_POST["partyAdmin"] != ""
			&& isset($_POST["userIDS"]) && $_POST["userIDS"] != ""){ 
         
        #Setup variables 
        $partyname = $_POST["partyname"]; 
		$partyAdmin = $_POST["partyAdmin"];
		$userIDS = $_POST["userIDS"];
		
		$timestamp = time();
		
        #Connect to Database 
        $con = mysqli_connect("localhost","root","", "kamer_sessie"); 
         
        #Check connection 
        if (mysqli_connect_errno()) { 
            echo 'Database connection error: ' . mysqli_connect_error(); 
            exit(); 
        } 

        #Escape special characters to avoid SQL injection attacks 
        $partyname = mysqli_real_escape_string($con, $partyname);
		$partyAdmin = mysqli_real_escape_string($con, $partyAdmin);
		$userIDS = mysqli_real_escape_string($con, $userIDS);		
         
        #Query the database to get the user details. 
        $userdetails = mysqli_query($con, "INSERT INTO party (partyId, partyname, partyAdmin, created) VALUES (NULL, '$partyname', '$partyAdmin', '$timestamp')");
		if($userdetails){
			$row = mysqli_fetch_row(mysqli_query($con, "SELECT * FROM party WHERE created = '$timestamp'"));
			$values = "";
			$explode = explode(":" , $userIDS );
			for ($i = 0; $i < count($explode) - 1; $i++) {
				$values.="(NULL, '{$explode[$i]}', '{$row[0]}'),";
			}
			$lastValue = $explode[count($explode)-1];
			$values.="(NULL, '{$lastValue}', '{$row[0]}')";
			$userdetails = mysqli_query($con, "INSERT INTO userhasparty (uhpid,userId, partyId) VALUES $values");
		}			

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