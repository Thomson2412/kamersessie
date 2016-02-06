<?php 

    #Ensure that the client has provided a value for "username" 
    if (isset($_POST["username"]) && $_POST["username"] != ""){ 
         
        #Setup variables 
        $username = $_POST["username"]; 
         
        #Connect to Database 
        $con = mysqli_connect("localhost","root","", "kamer_sessie"); 
         
        #Check connection 
        if (mysqli_connect_errno()) { 
            echo 'Database connection error: ' . mysqli_connect_error(); 
            exit(); 
        } 

        #Escape special characters to avoid SQL injection attacks 
        $username = mysqli_real_escape_string($con, $username); 
         
        #Query the database to get the user details. 
        $userdetails = mysqli_query($con, "SELECT * FROM user WHERE username = '$username'"); 

        #If no data was returned, check for any SQL errors 
        if (!$userdetails) { 
            echo 'Could not run query: ' . mysqli_error($con); 
            exit; 
        } 

        #Get the first row of the results 
        $row = mysqli_fetch_row($userdetails); 

        #Build the result array (Assign keys to the values) 
        $result_data = array(
			'userId' => $row[0],
            'username' => $row[1], 
            'password' => $row[2]
            ); 

        #Output the JSON data 
        echo json_encode($result_data);  
    }else{ 
        echo "Could not complete query. Missing parameter";  
    } 
?>