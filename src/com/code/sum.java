package com.code;

import com.wrx.utils.DBUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet({"/dept/list","/dept/add","/dept/edit","/dept/delet","/dept/detail"})

public class sum extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        String path=req.getServletPath();
        if(path.equals("/dept/list")) {
            dolist(req,res);
        }else if(path.equals("/dept/add")) {
            doAdd(req,res);
        }else if(path.equals("/dept/edit")) {
            try {
                doEdit(req,res);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else if(path.equals("/dept/delete")) {
            doDelet(req,res);
        }else if(path.equals("/dept/detail")) {
            doDetail(req,res);
        }
    }

    public void dolist(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("text/html;charset=utf-8");
        PrintWriter out = res.getWriter();

        String path=req.getContextPath();

        Connection coon=null;
        PreparedStatement ps=null;
        ResultSet rs=null;

        try {
            coon= DBUtil.getConnection();
            String sql="select * from dept";
            ps=coon.prepareStatement(sql);
            rs=ps.executeQuery();

            out.print(" <!DOCTYPE html>");
            out.print(" <html>");
            out.print(" <head>");
            out.print(" <meta charset='utf-8'>");
            out.print(" <title>部门列表页面</title>");
            out.print(" </head>");
            out.print(" <body>");

            out.print("<script type='text/javascript'>");
            out.print("function del(dno){");
            out.print("if(window.confirm('删除后不可恢复！')){");
            out.print("document.location.href='/project2/dept/delet?deptno='+dno");
            out.print("}");
            out.print("}");
            out.print("</script>");

            out.print(" <h1 align='center'>部门列表</h1>");
            out.print(" <hr >");
            out.print(" <table border='1' align='enter'>");
            out.print(" <tr>");
            out.print(" <th>序号</th>");
            out.print(" <th>部门编号</th>");
            out.print(" <th>部门名称</th>");
            out.print(" <th>操作</th>");
            out.print(" </tr>");

            int i=0;
            while (rs.next()) {
                String deptno=rs.getString("deptno");
                String dname=rs.getString("dname");
                String loc=rs.getString("loc");

                out.print(" <tr>");
                out.print(" <td>"+(++i)+"</td>");
                out.print(" <td>"+deptno+"</td>");
                out.print(" <td>"+dname+"</td>");
                out.print(" <td>");
                out.print(" <a href='javascript:void(0)' onclick='del("+deptno+")'>删除</a>");
                out.print(" <a href='edit.html'>修改</a>");
                out.print(" <a href='/project2/dept/detail?deptno="+deptno+"'>详情</a>");
                out.print(" </td>");
                out.print(" </tr>");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                DBUtil.close(coon,ps,rs);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        out.print(" </table>");
        out.print(" <hr>");
        out.print(" <a href='/project2/add.html'>新增部门</a>");
        out.print(" </body>");
        out.print(" </html> ");
    }

    public void doAdd(HttpServletRequest req, HttpServletResponse res) throws IOException{
        Connection coon = null;
        PreparedStatement ps = null;
        int count=0;

        String deptno = req.getParameter("deptno");
        String deptname = req.getParameter("deptname");
        String loc=req.getParameter("location");

        try {
            Connection connection = DBUtil.getConnection();
            String sql = "INSERT INTO dept(deptno,dname,loc) VALUES(?,?,?)";
            ps = connection.prepareStatement(sql);
            ps.setString(1, deptno);
            ps.setString(2, deptname);
            ps.setString(3, loc);
            count=ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally{
            try {
                DBUtil.close(coon,ps,null);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        if(count!=0){
            res.sendRedirect("/project2/dept/list");
        }
        else{
            res.sendRedirect("/project2/error.html");
        }
    }

    public void doDelet(HttpServletRequest req, HttpServletResponse res) throws IOException{
        Connection coon=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        int count=0;

        try {

            coon = DBUtil.getConnection();
            coon.setAutoCommit(false);

            String sql="DELETE FROM dept WHERE deptno=?";
            String id=req.getParameter("deptno");
            ps= coon.prepareStatement(sql);
            ps.setString(1,id);
            count = ps.executeUpdate();

            coon.commit();

        } catch (SQLException e) {
            if(coon!=null){
                try {
                    coon.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            throw new RuntimeException(e);
        }finally{
            try {
                DBUtil.close(coon,ps,rs);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        if(count!=0){
            //执行删除操作后，要进行部门列表的重新显示，此时要用的转发机制
            res.sendRedirect("/project2/dept/list");
        }else{
            res.sendRedirect("/project2/error.html");
        }
    }

    public  void doDetail(HttpServletRequest req, HttpServletResponse res) throws IOException{
        res.setContentType("text/html;charset=utf-8");
        PrintWriter out = res.getWriter();


        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='utf-8'>");
        out.println("<title>部门详情</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>部门详情</h1>");
        out.println("<hr>");
        out.println("<table border='1px' align='center'>");
        out.println("<tr>");
        out.println("<th>部门编号</th>");
        out.println("<th>部门名称</th>");
        out.println("<th>部门地点</th>v");
        out.println("</tr>");

        Connection coon=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        String id=req.getParameter("deptno");

        try {
            coon= DBUtil.getConnection();
            String sql="select * from dept where deptno=?";
            ps=coon.prepareStatement(sql);
            ps.setString(1, id);
            rs=ps.executeQuery();

            if(rs.next()){

                String dname=rs.getString("dname");
                String deptno=rs.getString("deptno");
                String los=rs.getString("loc");

                out.println("<tr>");
                out.println("<td>"+deptno+"</td>");
                out.println("<td>"+dname+"</td>");
                out.println("<td>"+los+"</td>");
                out.println("</tr>");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                DBUtil.close(coon,ps,rs);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        out.println("</table>");
        out.println("<hr>");
        out.println("<input type='button' value='后退' onclick='window.history.back()'/>");
        out.println("</body>");
        out.println("</html>");
    }

    public void doEdit(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        // 解决请求体的中文乱码问题。
        request.setCharacterEncoding("UTF-8");

        // 获取表单中的数据
        String deptno = request.getParameter("deptno");
        String dname = request.getParameter("dname");
        String loc = request.getParameter("loc");
        // 连接数据库执行更新语句
        Connection conn = null;
        PreparedStatement ps = null;
        int count = 0;
        try {
            conn = DBUtil.getConnection();
            String sql = "update dept set dname = ?, loc = ? where deptno = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, dname);
            ps.setString(2, loc);
            ps.setString(3, deptno);
            count = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, null);
        }

        if (count == 1) {
            response.sendRedirect(request.getContextPath() + "/dept/list");
        }
    }
}
