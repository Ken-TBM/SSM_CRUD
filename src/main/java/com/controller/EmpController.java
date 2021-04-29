package com.controller;
import com.bean.Employee;
import com.bean.Msg;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理员工CRUD请求
 */
@Controller
public class EmpController {

    @Autowired
    EmployeeService employeeService;


    /**
     * 单个/批量
     * 批量：1-2-3
     * 单个删除 1
     * @param ids
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/emp/{ids}",method = RequestMethod.DELETE)
    public Msg deleteEmp(@PathVariable("ids")String ids){
        if(ids.contains("-")){
            List<Integer> del_ids=new ArrayList<>();
            String[] str_ids = ids.split("-");
            for (String id : str_ids) {
                //组装id的集合
                int i= Integer.parseInt(id);
                del_ids.add(i);
            }
            employeeService.deleteBatch(del_ids);
        }else {
            Integer id = Integer.parseInt(ids);
            employeeService.deleteEmp(id);
        }

        return Msg.Success();
    }


    /**
     *
     * 如果直接发送ajax=put形式的请求
     * 问题：
     *  请求体中有数据；但是Employee对象封装不上
     *  sql拼串全为空
     *  原因：
     *  Tomcat：
     *      请求体中的数据，封装一个map
     *      request.getParameter("empName")就会从这个map中取
     *      SpringMVC封装POJO对象的时候，会把POJO中每个属性的值，request.getParameter("email");
     *AJAX发送PUT请求不能直接发
     *  PUT请求，请求体中的数据，request.getParameter("empName")拿不到
     *  Tomcat一看是PUT请求不会封装请求体中的数据为map，只有POST形式的请求才封装请求体
     *
     * 解决方案：
     * 我们要能支持发送PUT之类的请求还要封装请求体中的数据
     * 配置上FormContentFilter：
     * 作用将请求体中的数据解析包装成一个map
     * request被重新包装，request.getParameter()被重写，就会从自己的map中取数据
     * 更新员工信息
     * @param employee
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/emp/{empId}",method = RequestMethod.PUT)
    public Msg saveEmp(Employee employee, HttpServletRequest request){
        System.out.println("请求体中的值："+request.getParameter("gender"));
        System.out.println("将要更新的员工数据："+employee);
        employeeService.updateEmp(employee);
        return Msg.Success();
    }


    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    @RequestMapping(value = "/emp/{id}",method = RequestMethod.GET)
    @ResponseBody
    public Msg getEmp(@PathVariable Integer id){
        System.out.println("准备查询");
        Employee employee = employeeService.getEmp(id);
        System.out.println(employee);
        return  Msg.Success().add("emp",employee);
    }
    /**
     * 检测用户名是否可用
     * @param empName
     * @return
     */
    @ResponseBody
    @RequestMapping("/checkUser")
    public Msg CheckUser(@RequestParam("empName")String empName){
        //先判断用户名是否是合法的表达式
        String regx="(^[a-zA-Z0-9_-]{6,16}$)|(^[\\u2E80-\\u9FFF]{2,5})";
        if (!empName.matches(regx)){
            return Msg.fail().add("va_msg","后端：用户名可以是2-5位中文或者6-16位英文和数字的组合");
        }
        //数据库用户名重复校验
        boolean hasEmpName=employeeService.CheckUser(empName);
        if(hasEmpName){
            return Msg.Success();
        }else {
            return  Msg.fail().add("va_msg","用户名不可用");
        }
    }

    /**
     * 员工保存
     * 1.支持JSR303
     * 2.导入Hibernate-Validator
     * @param employee
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/emp",method = RequestMethod.POST)
    public Msg saveEmp(@Valid Employee employee, BindingResult result){
        if (result.hasErrors()){
            //校验失败,返回失败，在模态框中显示校验失败的错误信息
            Map<String,Object> map=new HashMap<>();
            List<FieldError> errors = result.getFieldErrors();
            for (FieldError fieldError : errors) {
                System.out.println("错误的字段名:"+fieldError.getField());
                System.out.println("错误信息:"+fieldError.getDefaultMessage());
                map.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            return Msg.fail().add("errorFields",map);
        }else{
            System.out.println("准备添加数据");
            employeeService.saveEmp(employee);
            System.out.println("添加一条数据");
            return Msg.Success();
        }

    }


    /**
     * 导入jackson包
     * 将ResponseBody注解的返回值转换为json形式的数据
     * @param pn
     * @param model
     * @return
     */
    @ResponseBody
    @RequestMapping("/emps")
    public Msg getEmpsWithJson(@RequestParam(value = "pn",defaultValue = "1")Integer pn, Model model){
        PageHelper.startPage(pn,5);
        //startPage后面紧跟的这个查询就是一个分页查询
        List<Employee> emps=employeeService.getAll();
        //使用pageInfo包装查询后的结果，只需要将pageInfo交给页面就行了
        //封装了详细的分页信息，包括有我们查询出来的数据，传入连续显示的页数
        PageInfo page=new PageInfo(emps,5);
        return Msg.Success().add("pageInfo",page);
    }




    /**
     * 查询 分页
     * @return
     */
    @RequestMapping("/emp")
    public String getEmployees(@RequestParam(value = "pn",defaultValue = "1")Integer pn, Model model){
        //这不是一个分页查询
        //引入pageHelper分页插件
        //在查询前需要调用，传入页码 ，以及每页的大小
        PageHelper.startPage(pn,5);
        //startPage后面紧跟的这个查询就是一个分页查询
        List<Employee> emps=employeeService.getAll();
        //使用pageInfo包装查询后的结果，只需要将pageInfo交给页面就行了
        //封装了详细的分页信息，包括有我们查询出来的数据，传入连续显示的页数
        PageInfo page=new PageInfo(emps,5);
        model.addAttribute("pageInfo",page);
        return "list";
    }
}
