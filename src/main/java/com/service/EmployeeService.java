package com.service;

import com.bean.Employee;
import com.bean.EmployeeExample;
import com.bean.Msg;
import com.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
    @Autowired
    EmployeeMapper employeeMapper;

    /**
     * 查询员工数据（分页查询）
     * @return
     */
    public List<Employee> getAll() {
        return  employeeMapper.selectByExampleWithDept(null);
    }

    /**
     * 员工保存方法
     * @param employee
     */
    public int saveEmp(Employee employee) {
        int i = employeeMapper.insertSelective(employee);
        return i;
    }

    /**
     * 检验用户名是否可用
     * @param empName
     * @return true:当前姓名可用 false当前姓名不可用
     */
    public boolean CheckUser(String empName) {
        EmployeeExample example = new EmployeeExample();
        EmployeeExample.Criteria criteria = example.createCriteria();
        criteria.andEmpNameEqualTo(empName);
        long count=employeeMapper.countByExample(example);
        return count==0;
    }


    /**
     * 按照员工id查询员工
     * @param id
     * @return
     */
    public Employee getEmp(Integer id) {
        Employee employee = employeeMapper.selectByPrimaryKey(id);
        return employee;
    }

    /**
     * 员工更新
     */
    public void updateEmp(Employee employee) {
        employeeMapper.updateByPrimaryKeySelective(employee);
    }

    public void deleteEmp(Integer id) {
        employeeMapper.deleteByPrimaryKey(id);
    }

    public void deleteBatch(List<Integer> ids) {
        EmployeeExample example=new EmployeeExample();
        EmployeeExample.Criteria criteria = example.createCriteria();
        //delete from xxx where emp_id in()
        criteria.andEmpIdIn(ids);
        employeeMapper.deleteByExample(example);
    }
}
