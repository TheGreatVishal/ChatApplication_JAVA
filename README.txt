-> Package Registration includes methods : 
{
    # constructor (String url, String username, String password, Connection con, Scanner sc)

    # methods : 
            1. public String generateHash(String inputString)
            2. public void dottedLine()
            3. public List<String> allUsers() throws Exception 
            4. public Boolean isUserNameTaken(String u_name) throws Exception 
            5. public void signUp() throws Exception
            6. public String fetchHashedPasswordOfUser(String user_name) throws Exception 
            7. public boolean login() throws Exception 
            8. public void setLoginStatus(int status, String user_Status) throws Exception
}

-> Client Handler class :