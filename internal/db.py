"""Add CockroachDB related functions"""
import psycopg2 as pscg


# def init():
#     try:
#         db_url = opt.dsn
#         conn = pscg.connect(db_url,
#                             application_name="$ docs_simplecrud_psycopg2",
#                             cursor_factory=psycopg2.extras.RealDictCursor)
#     except Exception as e:
#         logging.fatal("database connection failed")
#         logging.fatal(e)
#         return
