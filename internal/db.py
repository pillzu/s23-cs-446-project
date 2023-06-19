"""Add CockroachDB related functions"""
import psycopg2 as pscg
import logging

"""
init(url): Initialize cockroach DB connection.
    Parameters: 
        - url: Database connection URL
    Returns:
        A valid database connection if the given url is valid
    Throws Exception if:
        The given url is invalid
"""
def init(url):
    try:
        return pscg.connect(url,
                            application_name="$ docs_simplecrud_psycopg2")
    except Exception as e:
        logging.fatal("Database Connection Failed")
        logging.fatal(e)
        return None


"""
exec_DDL(conn, stmt): Executes the given DDL statement.
    Parameters: 
        - conn: the database connection
        - stmt: the executed DDL statement
    Returns:
        - True: if conn is valid and stmt is queried successfully
        - False: otherwise
    Throws Exception if:
        - the given database connection is None
        - the statement has format error
"""
def exec_DDL(conn, stmt) -> bool:
    try:
        with conn.cursor() as cur:
            cur.execute(stmt)
            conn.commit()
        return True
    except Exception as e:
        logging.fatal("Query Execution Failed")
        logging.fatal(f"Last Executed Query: {stmt}")
        logging.fatal(e)
        return False


"""
exec_DML(conn, stmt): Executes the given DML statement.
    Parameters: 
        - conn: the database connection
        - stmt: the executed DML statement
    Returns:
        - row: The query result, if conn is valid and stmt is queried successfully
        - None: otherwise
    Throws Exception if:
        - the given database connection is None
        - the statement has format error
"""
def exec_DML(conn, stmt):
    try:
        with conn.cursor() as cur:
            cur.execute(stmt)
            row = cur.fetchone()
            conn.commit()
            return row
    except Exception as e:
        logging.fatal("Query Execution Failed")
        logging.fatal(f"Last Executed Query: {stmt}")
        logging.fatal(e)
        return None


"""
create_tables(conn): Create all tables in the database.
    Parameters: 
        - conn: the database connection
    Returns:
        - True: if conn is valid and all tables are created
        - False: otherwise
    Throws Exception if:
        - the given database connection is None
"""
def create_tables(conn):
    try:
        if conn is None:
            raise Exception("Database Connection Not Initialized")

        # Users
        statement = "CREATE TABLE IF NOT EXISTS Users (" \
                    "user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), " \
                    "username VARCHAR(20) NOT NULL, " \
                    "password VARCHAR(20) NOT NULL, " \
                    "first_name VARCHAR(20) NOT NULL, " \
                    "last_name VARCHAR(20) NOT NULL, " \
                    "phone_no BIGINT NOT NULL, " \
                    "address VARCHAR(100) NOT NULL, " \
                    "email VARCHAR(50) NOT NULL, " \
                    "party_points INTEGER)"

        # TODO: Add all DDLs for CREATE TABLE
        exec_DDL(conn, statement)
        return True

    except Exception as e:
        logging.fatal("Creating Tables Failed")
        logging.fatal(e)
        return False


"""
add_new_user(conn, username, password, first_name, last_name, phone_no, address, email): Insert a new user with
given information into Users table. The user's uid will be generated randomly using gen_random_uuid(). The user
will also be created with a default party point of 0.
    Parameters: 
        - conn: the database connection
        - username: the user's account username (String with len <= 20)
        - password: the user's account password (String with len <= 20)
        - first_name: the user's first name (String with len <= 20)
        - last_name: the user's last name (String with len <= 20)
        - phone_no: the user's phone number (Long)
        - address: the user's home address (String with len <= 100)
        - email: the user's email address (String with len <= 50)
    Returns:
        - True: if conn is valid and the user is inserted successfully
        - False: otherwise
    Throws Exception if:
        - the given database connection is None
        - the user's information may have invalid fields
"""
def add_new_user(conn, username, password, first_name, last_name, phone_no, address, email) -> bool:
    try:
        if conn is None:
            raise Exception("Database Connection Not Initialized")

        statement = f"INSERT INTO Users VALUES (gen_random_uuid(), '{username}', '{password}', '{first_name}', '{last_name}', {phone_no}, '{address}', '{email}', 0)"

        exec_DDL(conn, statement)
        return True

    except Exception as e:
        logging.fatal("Adding new user to database failed")
        logging.fatal(e)
        return False


# POC-only code
# Hardcoded URL, only for POC
db_url = "postgresql://yanchen:9gAOcPaBx4tJJ3OAsD7G6A@vibees-db-11486.7tt.cockroachlabs.cloud:26257/VIBEES?sslmode=verify-full"
connection = init(db_url)
# create_tables(connection)
add_new_user(connection, "JM_Test", "123456", "Jerry", "Meng", 123456789, "N/A", "jerrymeng20@gmail.com")
rows = exec_DML(connection, "SELECT * FROM Users")
print(rows)
rows = exec_DDL(connection, "DELETE FROM Users")
