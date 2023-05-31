# Tradista Flow

Tradista Flow is a simple Java Workflow engine that can be included in any Java application.
Tradista Flow allows to create/update/delete workflows and execute them.

Tradista Flow is available in Maven Central, it can be added to your project by adding this in your pom.xml file:

```xml
<dependency>
  <groupId>finance.tradista.flow</groupId>
  <artifactId>tradista-flow</artifactId>
  <version>2.0.0</version>
</dependency>
```

Create and save a workflow very easily:

```java
Workflow wkf = new Workflow("SampleWorkflow");
Status statusOne = new Status(wkf, "s1");
Status statusTwo = new Status(wkf, "s2");
Action actionOne = new Action(wkf, "a1", statusOne, statusTwo);
WorkflowManager.saveWorkflow(wkf);
```

Link your objects to a workflow thanks to the WorkflowObject interface:

```java
public class MyObject implements WorkflowObject {

	private Status status;
	
	private String workflow;

        ...

	@Override
	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public String getWorkflow() {
		return workflow;
	}
	
	public void setWorkflow(String name) {
		this.workflow = name;
	}

	@Override
	public Status getStatus() {
		return status;
	} 
  
        ...  
  
}
```

Execute a workflow action on your objects:

```java
MyObject obj = new MyObject();
obj.setWorkflow("SampleWorkflow");
obj.setStatus(s2);
WorkflowManager.applyAction(obj, a1);
```

Tradista Flow is based on JPA. It can be used in JTA or non JTA mode.
It can be configured using a persistence.xml file in your classpath, please find below a sample in non JTA mode (JTA mode is commented),
using Hibernate and Derby:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="https://jakarta.ee/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="3.0">
    <persistence-unit name="tradista-flow-persistence-unit" transaction-type="RESOURCE_LOCAL">
        <description>This is a the unique persistence unit of the tradista flow project.</description>
        <class>finance.tradista.flow.model.Workflow</class>
	<class>finance.tradista.flow.model.Action</class>
	<class>finance.tradista.flow.model.SimpleAction</class>
	<class>finance.tradista.flow.model.ConditionalAction</class>
	<class>finance.tradista.flow.model.Condition</class>
	<class>finance.tradista.flow.model.Guard</class>
	<class>finance.tradista.flow.model.Process</class>
	<class>finance.tradista.flow.model.Status</class>
	<class>finance.tradista.flow.model.TradistaFlowObject</class>
        <properties>
            <property name="jakarta.persistence.jdbc.driver" value="org.apache.derby.jdbc.EmbeddedDriver" />
            <property name="jakarta.persistence.jdbc.url" value="jdbc:derby:memory:TradistaFlow;create=true" />
            <property name="jakarta.persistence.jdbc.user" value="xxxxx" />
            <property name="jakarta.persistence.jdbc.password" value="xxxxx" />
            <property name="hibernate.dialect" value="org.hibernate.dialect.DerbyDialect"/>
            <property name="hibernate.show_sql" value="true"/>
            <property name="javax.persistence.schema-generation.database.action" value="drop-and-create"/>
        </properties>
    </persistence-unit>
    <!--<persistence-unit name="tradista-flow-persistence-unit" transaction-type="JTA">
        <description>This is a the unique persistence unit of the tradista flow project.</description>
        <class>finance.tradista.flow.model.Workflow</class>
	<class>finance.tradista.flow.model.Action</class>
	<class>finance.tradista.flow.model.SimpleAction</class>
	<class>finance.tradista.flow.model.ConditionalAction</class>
	<class>finance.tradista.flow.model.Condition</class>
	<class>finance.tradista.flow.model.Guard</class>
	<class>finance.tradista.flow.model.Process</class>
	<class>finance.tradista.flow.model.Status</class>
	<class>finance.tradista.flow.model.TradistaFlowObject</class>
        <jta-data-source>java:/myAppDS</jta-data-source>
        <properties>
            <property name="hibernate.dialect" value="org.hibernate.dialect.DerbyDialect"/>
            <property name="hibernate.show_sql" value="true"/>
            <property name="javax.persistence.schema-generation.database.action" value="drop-and-create"/>
        </properties>
    </persistence-unit>-->
</persistence>
```

# Dependencies
Tradista Flow is made possible using powerful third party tools:
- [Apache Commons](https://commons.apache.org/) for various common needs
- [Apache Derby](https://db.apache.org/derby/) as database provider for test of the persistence layer
- [Apache Maven](https://maven.apache.org/) for the build
- [Hibernate](https://hibernate.org/) as JPA provider for test of the persistence layer
- [JGraphT](https://jgrapht.org) for graph modeling
- [JReleaser](https://jreleaser.org) for publication of the releases on Maven Central 
- [JUnit](https://junit.org/junit5) for unit testing
