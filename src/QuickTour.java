import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
public final class QuickTour {
    private QuickTour() {
    }

    public static void main(final String[] args) throws UnknownHostException {
        final Morphia morphia = new Morphia();

        // tell morphia where to find your classes
        // can be called multiple times with different packages or classes
        morphia.mapPackage("org.mongodb.morphia.example");
        MongoClientOptions options = MongoClientOptions.builder().connectTimeout(2000000000).build();
    	//2w1ubp7MOdN54-GOvvLHCXpegjfP3pwP
    	 char[] passWord = { '2', 'w', '1', 'u', 'b', 'p', '7',
 			    'M', 'O', 'd', 'N', '5', '4','-','G','O','v','v','L','H','C','X','p','e','g','j','f',
 			    'P','3','p','w','P' };
    	List<MongoCredential> credentialsList = new ArrayList<>();
    	credentialsList.add(MongoCredential.createCredential(
    					"CloudFoundry_h7d69dsj_tomptbqo_n6c18lsn", 
    					"CloudFoundry_h7d69dsj_tomptbqo", passWord));
    	MongoClient mongoClient= new MongoClient(new ServerAddress("ds023105.mlab.com",23105),credentialsList,options);
        
        // create the Datastore connecting to the database running on the default port on the local host
        final Datastore datastore = morphia.createDatastore(mongoClient, "CloudFoundry_h7d69dsj_tomptbqo");
        //datastore.getDB().dropDatabase();
        datastore.ensureIndexes();

        final Employee elmer = new Employee("Elmer Fudd",27, 50000.0);
        datastore.save(elmer);

        final Employee daffy = new Employee("Daffy Duck",30, 40000.0);
        datastore.save(daffy);

        final Employee pepe = new Employee("Pepé Le Pew", 40,25000.0);
        datastore.save(pepe);

        Query<Employee> query = datastore.createQuery(Employee.class);
        final List<Employee> employees = query.asList();

        System.out.println( employees.size());

        List<Employee> underpaid = datastore.createQuery(Employee.class)
                                            .filter("salary <=", 30000)
                                            .asList();
        System.out.println( underpaid.size());

        underpaid = datastore.createQuery(Employee.class)
                             .field("salary").lessThanOrEq(30000)
                             .asList();
        System.out.println( underpaid.size());

        final Query<Employee> underPaidQuery = datastore.createQuery(Employee.class)
                                                        .filter("salary <=", 30000);
        final UpdateOperations<Employee> updateOperations = datastore.createUpdateOperations(Employee.class)
                                                                     .inc("salary", 10000);

        final UpdateResults results = datastore.update(underPaidQuery, updateOperations);

        System.out.println(results.getUpdatedCount());

        final Query<Employee> overPaidQuery = datastore.createQuery(Employee.class)
                                                       .filter("salary >", 100000);
        datastore.delete(overPaidQuery);
    }
}

@Entity("employees")
@Indexes(@Index(value = "salary", fields = @Field("salary")))
class Employee {
    private String name;
    private Integer age;
    private Double salary;

    public Employee() {
    }

    public Employee(final String name,final Integer age, final Double salary) {
        this.name = name;
        this.salary = salary;
        this.age = age;
    }
}
