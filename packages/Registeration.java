package packages;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Registeration {

    String url;
    String username;
    String password;
    Connection con;

    public Registeration(String url, String username, String password, Connection con) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.con = con;
    }

    public String generateHash(String inputString) {

        String hexString = "";
        try {
            // Create MessageDigest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Perform hashing on the input string
            byte[] hashBytes = digest.digest(inputString.getBytes());

            // Convert byte array to hexadecimal format
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString += '0';
                }
                hexString += hex;
            }

        } catch (Exception e) {
            System.err.println("Error during hashing: " + e.getMessage());
        }
        return hexString;
    }

    public void dottedLine() {
        for (int i = 1; i <= 30; i++) {
            System.out.print("--");
        }
    }

    public List<String> allUsers() throws Exception {
        List<String> users = new ArrayList<>();
        String query = "select * from users";

        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                users.add(rs.getString(2));
            }
        } catch (Exception e) {
            System.out.println("Error occured inside allUsers() : " + e);
        }

        // System.out.println("\nInside all users : 3");
        return users;
    }

    public Boolean isUserNameTaken(String u_name) throws Exception {
        Boolean Flag = false;
        List<String> users = allUsers();

        for (String user : users) {
            if (u_name.equals(user)) {
                Flag = true;
            }
        }
        return Flag;
    }

    public void signUp(String u_name, String pass) throws Exception {

        String hashedPassword = generateHash(pass);
        try {
            String sqlQuery = "INSERT INTO users (username, password) VALUES ('" + u_name
                    + "', ' " + hashedPassword
                    + "')";
            Statement st = con.createStatement();
            st.executeUpdate(sqlQuery);

            con.close();

        } catch (Exception e) {
            System.out.print("\nAccount creation Failed !!");
            System.out.print("\n" + e + "\n");
        }
        dottedLine();
    }

    public String fetchHashedPasswordOfUser(String user_name) throws Exception {
        String returnValue = null;

        String query = "select password from users where username='" + user_name + "'";

        Statement st = this.con.createStatement();
        ResultSet rs = st.executeQuery(query);
        rs.next();
        returnValue = rs.getString(1);

        return returnValue;
    }

    public int login(String u_name, String pass) throws Exception {

        String hashedPassword = generateHash(pass);

        if (isUserNameTaken(u_name)) {
            String pass_of_u_name = fetchHashedPasswordOfUser(u_name);
            pass_of_u_name = pass_of_u_name.trim();

            if (pass_of_u_name.compareTo(hashedPassword) == 0) {
                System.out.println("Login successfully");
                return 1;
            } else {
                System.out.println("Incorrect password");
            }
        } else {
            System.out.println("Username doesn't Exist");
        }

        return -1;
    }

    public void setLoginStatus(int status, String user) throws Exception {
        String sqlQuery = "UPDATE users SET login_status = " + status + " WHERE username = '" + user + "'";
        Statement st = con.createStatement();
        st.executeUpdate(sqlQuery);
    }

    public int isUserOnline(String u_name) throws Exception {

        int returnValue;

        String query = "SELECT login_status FROM users WHERE username = '" + u_name + "'";

        Statement st = this.con.createStatement();
        ResultSet rs = st.executeQuery(query);
        rs.next();
        returnValue = Integer.parseInt(rs.getString(1));

        return returnValue;
    }

}