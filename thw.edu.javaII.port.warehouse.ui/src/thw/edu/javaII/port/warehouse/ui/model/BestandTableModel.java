package thw.edu.javaII.port.warehouse.ui.model;

import java.io.Serial;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import thw.edu.javaII.port.warehouse.model.LagerBestand;

public class BestandTableModel extends AbstractTableModel {

	@Serial
	private static final long serialVersionUID = -6145988449443265248L;
	private List<LagerBestand> data;

	public BestandTableModel(List<LagerBestand> data) {
		this.data = data;
	}

	public void setData(List<LagerBestand> data) {
		this.data = data;
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		return 6; // Fest auf 6 Spalten setzen, passend zu getColumnName
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		LagerBestand bestand = data.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> bestand.getId();
            case 1 -> bestand.getProdukt_id() != null ? bestand.getProdukt_id().getName() : "-";
            case 2 -> bestand.getProdukt_id() != null ? bestand.getProdukt_id().getHersteller() : "-";
            case 3 -> bestand.getAnzahl();
            case 4 -> bestand.getLagerplatz_id() != null ? bestand.getLagerplatz_id().getName() : "-";
            case 5 -> (bestand.getLagerplatz_id() != null && bestand.getLagerplatz_id().getLager_id() != null)
                    ? bestand.getLagerplatz_id().getLager_id().getName() : "-";
            default -> null;
        };
	}

	@Override
	public String getColumnName(int arg0) {
        return switch (arg0) {
            case 0 -> "ID";
            case 1 -> "Produkt";
            case 2 -> "Hersteller";
            case 3 -> "Lagerbestand";
            case 4 -> "Lagerplatz";
            case 5 -> "Lager";
            default -> null;
        };
	}

	public void setJTableColumnsWidth(JTable table, int tablePreferredWidth, double... percentages) {
		double total = 0;
		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			total += percentages[i];
		}

		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			TableColumn column = table.getColumnModel().getColumn(i);
			column.setPreferredWidth((int) (tablePreferredWidth * (percentages[i] / total)));
		}
	}

	public LagerBestand getObjectAt(int selectedRow) {
		return data.get(selectedRow);
	}
}