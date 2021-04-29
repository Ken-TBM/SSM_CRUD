import com.bean.Department;
import com.bean.Employee;
import com.mapper.DepartmentMapper;
import com.mapper.EmployeeMapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.swing.*;
import java.util.UUID;

/**
 * 测试departmentMapper
 * 推荐：Spring的项目就可以使用Spring单元测试，leukemia自动注入我们
 * 需要的组件
 * 1、导入SpringTest模块
 * 2、@ContextConfiguration指定spring配置文件的位置
 * 3、直接Autowired
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring1/applicationContext.xml"})
public class MapperTest {

   @Autowired
    DepartmentMapper departmentMapper;

   @Autowired
   EmployeeMapper employeeMapper;

   @Autowired
   SqlSession sqlSession;
    @Test
    public void testCRUD(){
        /*//1.创建springIOC容器
        ClassPathXmlApplicationContext ioc = new ClassPathXmlApplicationContext("spring/applicationContext.xml");
        //2.从容器中获取mapper
        DepartmentMapper bean = ioc.getBean(DepartmentMapper.class);
        System.out.println(bean);*/
        System.out.println(departmentMapper);
        //1、插入几个部门
//       departmentMapper.insertSelective(new Department(null,"开发部"));
//       departmentMapper.insertSelective(new Department(null,"测试部"));
       //2、生成员工数据，测试员工插入
        //employeeMapper.insertSelective(new Employee(null,"Jerry","M","Jerry@123.com",1));
        //3、批量插入多个员工；批量，使用可以批量操作的sqlSession
        EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
        for (int i = 0; i < 1000; i++) {
            String uid = UUID.randomUUID().toString().substring(0, 5)+i;
            mapper.insert(new Employee(null,uid,"M",uid+"@123.com",1));
        }
        System.out.println("批量完成");

    }
}