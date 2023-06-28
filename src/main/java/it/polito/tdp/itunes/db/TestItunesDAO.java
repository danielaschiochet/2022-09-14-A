package it.polito.tdp.itunes.db;

public class TestItunesDAO {

	public static void main(String[] args) {
		ItunesDAO dao = new ItunesDAO();
		
		System.out.println(dao.getAlbumsWithDuration(0.0).size());
		System.out.println(dao.getAlbumsWithDuration(5.0).size());
		System.out.println(dao.getAlbumsWithDuration(60.0).size());

		
	}

}
