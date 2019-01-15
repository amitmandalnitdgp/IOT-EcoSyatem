import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
@WebServlet("/HbaseConnection")
public class HbaseConnection extends HttpServlet {
    private static final long serialVersionUID = 1L;
 
    public HbaseConnection() {
        super();
 
    }
 
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
  
    }
 
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	response.setContentType("text/html;charset=UTF-8");
    	
        try {
        	String sensType = request.getParameter("sensType");
            String result = queryHbase(sensType);
            
            response.setStatus(HttpServletResponse.SC_OK);
            OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
            
            writer.write(result);
            writer.flush();
            writer.close();
  
        } catch (IOException e) {
 
             try{
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print(e.getMessage());
                response.getWriter().close();
            } catch (IOException ioe) {
            }
        }   
    }
    
 public String queryHbase(String sensType) {
	 double val=0.0;
	 String result = "";
	 try {
		Class.forName("org.apache.drill.jdbc.Driver");
		Connection connection = DriverManager.getConnection("jdbc:drill:zk=192.168.1.6:2181/drill/drillbits1");
		Statement st = connection.createStatement();

		ResultSet rs1 = st.executeQuery("SELECT CONVERT_FROM(SensData." + sensType + ", 'UTF8') FROM hbase.SensData");
	      
	      int count=0;
	      while(rs1.next()){

		         System.out.println("-->"+rs1.getString(1));
		         float temp = Float.parseFloat(rs1.getString(1));
		         val = val+temp;
		         count++;
		      }
	      val = val/count*100;
	      val = Math.round(val);
	      val = val/100;
		
		
	} catch (ClassNotFoundException | SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 
	 return ""+val;
	 
 }
}