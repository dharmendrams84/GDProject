package ignitiv.gd;

public class Employee
{
  private String employeeNumber;
  private String employeeFirstName;
  private String employeeLastName;
  private String birthDate;
  private String email;
  private String cellphone;
  private String empStatusName;
  private String empStatusXRef;
  private String supervisorName;
  private String jobName;
  private String homeStoreNumber;
  private String employeeDiscountGroup;
  private String xRef;
  
  public Employee() {}
  
  public Employee(String employeeNumber, String employeeFirstName, String employeeLastName, String birthDate, String email, String cellphone, String empStatusName, String empStatusXRef, String supervisorName, String jobName, String homeStoreNumber, String employeeDiscountGroup, String xRef)
  {
    this.employeeNumber = employeeNumber;
    this.employeeFirstName = employeeFirstName;
    this.employeeLastName = employeeLastName;
    this.birthDate = birthDate;
    this.email = email;
    this.cellphone = cellphone;
    this.empStatusName = empStatusName;
    this.empStatusXRef = empStatusXRef;
    this.supervisorName = supervisorName;
    this.jobName = jobName;
    this.homeStoreNumber = homeStoreNumber;
    this.employeeDiscountGroup = employeeDiscountGroup;
    this.xRef = xRef;
  }
  
  public String getEmployeeNumber()
  {
    return this.employeeNumber;
  }
  
  public void setEmployeeNumber(String employeeNumber)
  {
    this.employeeNumber = employeeNumber;
  }
  
  public String getEmployeeFirstName()
  {
    return this.employeeFirstName;
  }
  
  public void setEmployeeFirstName(String employeeFirstName)
  {
    this.employeeFirstName = employeeFirstName;
  }
  
  public String getEmployeeLastName()
  {
    return this.employeeLastName;
  }
  
  public void setEmployeeLastName(String employeeLastName)
  {
    this.employeeLastName = employeeLastName;
  }
  
  public String getBirthDate()
  {
    return this.birthDate;
  }
  
  public void setBirthDate(String birthDate)
  {
    this.birthDate = birthDate;
  }
  
  public String getEmail()
  {
    return this.email;
  }
  
  public void setEmail(String email)
  {
    this.email = email;
  }
  
  public String getCellphone()
  {
    return this.cellphone;
  }
  
  public void setCellphone(String cellphone)
  {
    this.cellphone = cellphone;
  }
  
  public String getEmpStatusName()
  {
    return this.empStatusName;
  }
  
  public void setEmpStatusName(String empStatusName)
  {
    this.empStatusName = empStatusName;
  }
  
  public String getEmpStatusXRef()
  {
    return this.empStatusXRef;
  }
  
  public void setEmpStatusXRef(String empStatusXRef)
  {
    this.empStatusXRef = empStatusXRef;
  }
  
  public String getSupervisorName()
  {
    return this.supervisorName;
  }
  
  public void setSupervisorName(String supervisorName)
  {
    this.supervisorName = supervisorName;
  }
  
  public String getJobName()
  {
    return this.jobName;
  }
  
  public void setJobName(String jobName)
  {
    this.jobName = jobName;
  }
  
  public String getHomeStoreNumber()
  {
    return this.homeStoreNumber;
  }
  
  public void setHomeStoreNumber(String homeStoreNumber)
  {
    this.homeStoreNumber = homeStoreNumber;
  }
  
  public String getEmployeeDiscountGroup()
  {
    return this.employeeDiscountGroup;
  }
  
  public void setEmployeeDiscountGroup(String employeeDiscountGroup)
  {
    this.employeeDiscountGroup = employeeDiscountGroup;
  }
  
  public String getxRef()
  {
    return this.xRef;
  }
  
  public void setxRef(String xRef)
  {
    this.xRef = xRef;
  }
  
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("Employee Details - ");
    sb.append("Employee Number:" + getEmployeeNumber());
    sb.append(", ");
    sb.append("Employee First Name:" + getEmployeeFirstName());
    sb.append(", ");
    sb.append("Employee Last Name:" + getEmployeeLastName());
    sb.append(", ");
    sb.append("Birth Date:" + getBirthDate());
    sb.append(", ");
    sb.append("Email:" + getEmail());
    sb.append(", ");
    sb.append("Cellphone:" + getCellphone());
    sb.append(", ");
    sb.append("Emp Status Name:" + getEmpStatusName());
    sb.append(", ");
    sb.append("Emp Status XRef:" + getEmpStatusXRef());
    sb.append(", ");
    sb.append("Supervisor Name:" + getSupervisorName());
    sb.append(", ");
    sb.append("Job Name:" + getJobName());
    sb.append(", ");
    sb.append("Home Store Number:" + getHomeStoreNumber());
    sb.append(", ");
    sb.append("Employee Discount Group:" + getEmployeeDiscountGroup());
    sb.append(", ");
    sb.append("xRef:" + getxRef());
    sb.append(".");
    
    return sb.toString();
  }
}
