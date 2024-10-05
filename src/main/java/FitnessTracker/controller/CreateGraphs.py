import mysql.connector
import matplotlib.pyplot as plt
import numpy as np
import io
import sys

db_config = {
    'host': 'localhost',
    'user': 'springstudent',
    'password': 'springstudent',
    'database': 'fitness_tracker_db'
}

try:
    connection = mysql.connector.connect(**db_config)

    if connection.is_connected():
        print("Connection successful!")

        cursor = connection.cursor()

        query = """
        SELECT exercise_name,
               GROUP_CONCAT(weight ORDER BY date) AS weights,
               GROUP_CONCAT(date ORDER BY date) AS dates FROM exercise GROUP BY exercise_name
        """

        # Execute the query
        cursor.execute(query)
        results = cursor.fetchall()

        # Process results for plotting
        for i in range(len(results)):
            exercise_name = results[i][0]
            weights = list(map(float, results[i][1].split(',')))  # Convert weights to a list of floats
            dates = results[i][2].split(',')  # Split dates into a list

            # Create a new figure for each exercise
            plt.figure(figsize=(10, 6))

            # Plot the data
            plt.plot(dates, weights, marker='o', label=exercise_name)

            # Customize the plot
            plt.title(f'{exercise_name} Progression Graph')
            plt.xlabel('Date')
            plt.ylabel('Weights (kg)')
            plt.xticks(rotation=45)
            plt.legend()
            plt.grid()
            plt.tight_layout()

            # Save the figure to a buffer instead of a file
            buffer = io.BytesIO()
            plt.savefig(buffer, format='png')
            buffer.seek(0)  # Rewind the buffer to the beginning

            # Print buffer contents to stdout to send back to Java
            sys.stdout.buffer.write(buffer.getvalue())
            plt.close()  # Close the figure

except mysql.connector.Error as err:
    print(f"Error: {err}")
finally:
    if 'cursor' in locals():
        cursor.close()
    if 'connection' in locals() and connection.is_connected():
        connection.close()
