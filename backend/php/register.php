<?php 

    #Ensure that the client has provided a value for "username" 
    if (isset($_POST["username"]) && $_POST["username"] != ""
			&& isset($_POST["password"]) && $_POST["password"] != ""){ 
         
        #Setup variables 
        $username = $_POST["username"]; 
		$password = $_POST["password"];
		
		$timestamp = time();
		
        #Connect to Database 
        $con = mysqli_connect("localhost","root","", "kamer_sessie"); 
         
        #Check connection 
        if (mysqli_connect_errno()) { 
            echo 'Database connection error: ' . mysqli_connect_error(); 
            exit(); 
        } 

        #Escape special characters to avoid SQL injection attacks 
        $username = mysqli_real_escape_string($con, $username);
		$password = mysqli_real_escape_string($con, $password);		
         
        #Query the database to get the user details. 
        $userdetails = mysqli_query($con, "INSERT INTO user (userId, username, password) VALUES (NULL, '$username', '$password')");
		

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