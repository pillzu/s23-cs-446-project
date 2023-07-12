from db import DatabaseConnection
from decouple import config
import logging

import psycopg2 as pscg

# Connect to the database
db_url = config("CDB_URL")
conn = pscg.connect(db_url, application_name="$ docs_simplecrud_psycopg2")

# Create a cursor
cur = conn.cursor()

# Define the mapping of the new tables and their corresponding columns
new_table_mappings = {
    "users": [
        "user_id UUID PRIMARY KEY DEFAULT gen_random_uuid()",
        "profile_url VARCHAR(100)",
        "first_name VARCHAR(20) NOT NULL",
        "last_name VARCHAR(20) NOT NULL",
        "phone_no BIGINT NOT NULL",
        "address_street VARCHAR(50) NOT NULL",
        "address_city VARCHAR(10) NOT NULL",
        "address_prov VARCHAR(10) NOT NULL",
        "address_postal VARCHAR(10) NOT NULL",
        "email VARCHAR(50) NOT NULL",
        "party_points INTEGER"
    ],
    "parties": [
        "party_id UUID PRIMARY KEY DEFAULT gen_random_uuid()",
        "party_avatar_url VARCHAR(100)",
        "party_name VARCHAR(50) NOT NULL",
        "date_time TIMESTAMP NOT NULL",
        "host_id UUID",
        "created_at TIMESTAMP NOT NULL",
        "max_capacity INTEGER NOT NULL",
        "description VARCHAR(250) NOT NULL",
        "UNIQUE(party_name)",
        "entry_fee INTEGER",
        "CONSTRAINT host_user_id FOREIGN KEY (host_id) REFERENCES new_users(user_id) ON DELETE SET NULL"
    ],
    "tags": [
        "party_id UUID",
        "UNIQUE(party_id)",
        "tag_list VARCHAR[] NOT NULL",
        "CONSTRAINT party_id FOREIGN KEY (party_id) REFERENCES new_parties(party_id) ON DELETE CASCADE"
    ],
    "partylocations": [
        "party_id UUID",
        "UNIQUE(party_id)",
        "street VARCHAR(100) NOT NULL",
        "city VARCHAR(50) NOT NULL",
        "prov VARCHAR(50) NOT NULL",
        "postal_code VARCHAR(20) NOT NULL",
        "UNIQUE(street, city, prov, postal_code)",
        "CONSTRAINT party_id FOREIGN KEY (party_id) REFERENCES new_parties(party_id) ON DELETE CASCADE"
    ],
    "musicsuggestions": [
        "guest_id UUID",
        "party_id UUID",
        "UNIQUE(guest_id, party_id)",
        "suggested_tracks VARCHAR[] NOT NULL",
        "CONSTRAINT party_id FOREIGN KEY (party_id) REFERENCES new_parties(party_id) ON DELETE CASCADE",
        "CONSTRAINT guest_id FOREIGN KEY (guest_id) REFERENCES new_users(user_id) ON DELETE CASCADE"
    ],
    "transactions": [
        "transaction_id UUID PRIMARY KEY",
        "time TIMESTAMP",
        "from_id UUID",
        "to_id UUID",
        "guest_id UUID",
        "party_id UUID",
        "payment_amount DECIMAL(10, 2)",
        "CONSTRAINT guest_user_id FOREIGN KEY (from_id) REFERENCES new_users(user_id) ON DELETE CASCADE",
        "CONSTRAINT host_user_id FOREIGN KEY (to_id) REFERENCES new_users(user_id) ON DELETE CASCADE",
        "CONSTRAINT party_id FOREIGN KEY (party_id) REFERENCES new_parties(party_id) ON DELETE CASCADE"
    ]
}

# Do the same for the old tables, but with only the names of the columns
old_table_mappings = {
    "users": [
        "user_id",
        "username",
        "password",
        "first_name",
        "last_name",
        "phone_no",
        "address_street",
        "address_city",
        "address_prov",
        "address_postal",
        "email",
        "party_points"
    ],
    "parties": [
        "party_id",
        "party_name",
        "date_time",
        "created_at",
        "max_capacity",
        "description",
        "entry_fee"
    ],
    "tags": [
        "party_id",
        "tag_list"
    ],
    "partylocations": [
        "party_id",
        "street",
        "city",
        "prov",
        "postal_code"
    ],
    "musicsuggestions": [
        "guest_id",
        "party_id",
        "suggested_tracks"
    ],
    "transactions": [
        "transaction_id",
        "time",
        "from_id",
        "to_id",
        "party_id",
        "payment_amount"
    ]
}

# Migrate the data for each table
for table, columns in new_table_mappings.items():
    new_table = "new_" + table
    # Fetch data from the source table
    cur.execute(f"SELECT {', '.join(old_table_mappings[table])} FROM {table}")
    rows = cur.fetchall()

    # Create the new table with the updated schema
    cur.execute(f"CREATE TABLE IF NOT EXISTS {new_table} ({', '.join(columns)})")
    
    # Insert data into the new table
    for row in rows:
        placeholders = ', '.join(['%s'] * len(row))
        # Check if it's the users table and insert a default profile url
        if new_table == "new_users":
            placeholders = ', '.join(['%s'] * (len(row) - 1))
            
            # TODO: Insert url to default profile here, replacing None value
            default_profile_url = None
            
            cur.execute(f"INSERT INTO {new_table} VALUES ({placeholders})", 
                        (row[0], default_profile_url, row[3], row[4], row[5], row[6], row[7], row[8], row[9], row[10], row[11]))

        # Check if it's the parties table and insert host_id from Hosts table as well as a default party avatar url
        elif new_table == "new_parties":
            placeholders = ', '.join(['%s'] * (len(row) + 2))
            host_id_query = "SELECT host_id FROM hosts WHERE party_id = %s"
            cur.execute(host_id_query, (row[0],))
            try:
                host_id = cur.fetchone()[0]
            except TypeError:
                host_id = None

            # TODO: Insert url to default party avatar here, replacing None value
            default_party_avatar_url = None
            
            cur.execute(f"INSERT INTO {new_table} VALUES ({placeholders})", 
                        (row[0], default_party_avatar_url, row[1], row[2], host_id, row[3], row[4], row[5], row[6]))
            
        # Check if it's the transactions table and insert guest_id from Guests table
        elif new_table == "new_transactions":
            guest_id_query = "SELECT guest_id FROM guests WHERE party_id = %s"
            cur.execute(guest_id_query, (row[4],))
            try:
                guest_id = cur.fetchone()[0]
            except TypeError:
                guest_id = None
            cur.execute(f"INSERT INTO {new_table} (transaction_id, time, guest_id, party_id, payment_amount, from_id, to_id) VALUES (%s, %s, %s, %s, %s, %s, %s)",
                        (row[0], row[1], guest_id, row[4], row[5], row[2], row[3]))
        else:
            cur.execute(f"INSERT INTO {new_table} VALUES ({placeholders})", row)            

# Commit the changes and close the cursor and connection
conn.commit()
cur.close()
conn.close()
