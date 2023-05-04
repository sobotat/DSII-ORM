module DSII.ORM {

    requires lombok;
    requires org.apache.logging.log4j;

    requires com.oracle.database.jdbc;
    requires java.sql;

    requires org.json;
    requires com.google.gson;

    exports org.dsII.orm;
    exports org.dsII.orm.db;
    exports org.dsII.orm.domain;
}