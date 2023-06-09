package data.collectionTools;

public class Queries {
    public static final String start = """
                        SELECT b.band_id, b.band_name, b.genre, b.x_coordinate, b.y_coordinate, b.studio_name, b.nop, b.creation_date, b.user_id
                        FROM bands b
                        ORDER BY b.band_id;
                        """;

    public static final String insertCoordinates = """
                        INSERT INTO coordinates (x, y)
                                VALUES (?, ?);
                        """;

    public static final String addBand = """
            insert into bands (Band_Name, Genre, X_Coordinate, Y_Coordinate, Studio_Name, NOP, Creation_Date, User_ID)
                    values (?, ?, ?, ?, ?, ?, ?, ?);
            """;

    public static final String updateBand = """
                        UPDATE bands
                        SET Band_Name = ?, Genre = ?, X_Coordinate = ?, Y_Coordinate = ?, Studio_Name = ?, NOP = ?, Creation_Date = ?, User_ID = ?
                        WHERE band_id = ?;
                        """;

    public static final String removeBand = """
                        DELETE FROM bands
                        WHERE band_id = ?;
                        """;
    public static final String removeBandsOfUser = """
                        DELETE FROM bands
                        WHERE user_id = ?;
                        """;

    public static final String reorderBands = """
                        SELECT *
                        FROM bands
                        ORDER BY band_id DESC;
                        """;

    public static final String current = """
                        SELECT currval(?);
                        """;

    public static final String maxID = """
                        select max(band_id) from bands;
                        """;

    public static final String isExists = """
                        SELECT band_id
                        FROM bands
                        WHERE band_id = ?;
                        """;
}


