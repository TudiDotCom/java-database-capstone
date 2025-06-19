This Spring Boot application uses both MVC and REST controllers. Thymeleaf templates are used for the Admin and Doctor dashboards, while REST APIs serve all other modules. The application interacts with two databases—MySQL (for patient, doctor, appointment, and admin data) and MongoDB (for prescriptions). All controllers route requests through a common service layer, which in turn delegates to the appropriate repositories. MySQL uses JPA entities while MongoDB uses document models.


Smart Clinic Management System is a Spring Boot app using Spring MVC with Thymeleaf for Admin and Doctor dashboards, and REST APIs for other modules. It connects to two databases: MySQL (via JPA for structured data like patients and appointments) and MongoDB (for prescription documents). All controllers delegate to a shared service layer, which interacts with repositories for data access.



1. User accesses Admin or Doctor dashboards (Thymeleaf pages) or other modules via REST API calls.  
2. Requests are routed to either Spring MVC controllers (for dashboards) or REST controllers (for APIs).  
3. Controllers forward the requests to a common service layer handling business logic.  
4. The service layer determines which repository to use based on the data type and operation.  
5. For structured data (patients, doctors, appointments, admins), the service interacts with JPA repositories connected to MySQL.  
6. For unstructured prescription data, the service uses MongoDB repositories with document models.  
7. Responses or views are returned back through the controllers to the user or client application.

