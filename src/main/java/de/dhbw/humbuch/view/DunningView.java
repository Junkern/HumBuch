package de.dhbw.humbuch.view;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.inject.Inject;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.StateChangeListener;
import de.davherrmann.mvvm.ViewModelComposer;
import de.davherrmann.mvvm.annotations.BindState;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Dunning;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.util.PDFDunning;
import de.dhbw.humbuch.util.PDFHandler;
import de.dhbw.humbuch.view.components.PrintingComponent;
import de.dhbw.humbuch.viewmodel.DunningViewModel;
import de.dhbw.humbuch.viewmodel.DunningViewModel.StudentsDunned;
import de.dhbw.humbuch.viewmodel.DunningViewModel.StudentsToDun;

public class DunningView extends VerticalLayout implements View,
		ViewInformation {

	private static final long serialVersionUID = 1284094636968999625L;

	private static final String TITLE = "Mahnungs Übersicht";
	private static final String SECOND_DUNNING = "2. Mahnung erzeugen";
	private static final String NEW_DUNNING = "1. Mahnung erzeugen";

	// TODO: Dynamically / directly from database
	private static final String TABLE_LAST_NAME = "Nachname";
	private static final String TABLE_FIRST_NAME = "Vorname";
	private static final String TABLE_CLASS = "Klasse";
	private static final String TABLE_TYPE = "Typ";

	@BindState(StudentsDunned.class)
	public final State<Collection<Dunning>> studentsDunned = new BasicState<>(
			Collection.class);
	@BindState(StudentsToDun.class)
	public final State<Collection<Dunning>> studentsToDun = new BasicState<>(
			Collection.class);

	private Map<Integer,Dunning> allDunnings = new HashMap<>();
	private Dunning selectedDunning;
	private DunningViewModel dunningViewModel;
	private HorizontalLayout horizontalLayoutButtonBar;
	private Button buttonSecondDunning;
	private Button buttonNewDunning;
	private Table tableDunnings;

	@Inject
	public DunningView(ViewModelComposer viewModelComposer,
			DunningViewModel dunningViewModel) {
		this.dunningViewModel = dunningViewModel;
		init();
		buildLayout();
		bindViewModel(viewModelComposer, dunningViewModel);
	}

	private void addListener() {
		StateChangeListener stateChange = new StateChangeListener() {
			@Override
			public void stateChange(Object value) {
				Collection<Dunning> tableData = (Collection<Dunning>) value;
				for (Dunning dunning : tableData) {
					allDunnings.put(dunning.getId(), dunning);
					tableDunnings.addItem(new Object[] {
							dunning.getStudent().getFirstname(),
							dunning.getStudent().getLastname(),
							dunning.getStudent().getGrade().toString(),
							dunning.getType().toString() + " " + dunning.getStatus().toString() },
							dunning.getId());
				}
			}

		};
		studentsDunned.addStateChangeListener(stateChange);
		studentsToDun.addStateChangeListener(stateChange);
		tableDunnings.setImmediate(true);
		tableDunnings.addValueChangeListener(new Table.ValueChangeListener() {
			private static final long serialVersionUID = -4224382328843243771L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(tableDunnings.size()==0) return;
				selectedDunning = allDunnings.get(Integer.parseInt(tableDunnings.getValue().toString()));
				if(selectedDunning.getType() == Dunning.Type.TYPE1 && selectedDunning.getStatus() == Dunning.Status.OPENED) {
					buttonNewDunning.setEnabled(true);
					buttonSecondDunning.setEnabled(false);
				}
				else if(selectedDunning.getType() == Dunning.Type.TYPE2 && selectedDunning.getStatus() == Dunning.Status.OPENED) {
					buttonNewDunning.setEnabled(false);
					buttonSecondDunning.setEnabled(true);
				}
				else {
					buttonNewDunning.setEnabled(false);
					buttonSecondDunning.setEnabled(false);
				}
			}
		});
		buttonNewDunning.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 8123444488274722661L;

			@Override
			public void buttonClick(ClickEvent event) {
				Set<Student> setStudent = new HashSet<Student>();
				setStudent.add(selectedDunning.getStudent());
				List<BorrowedMaterial> listBorrowedMaterial = new ArrayList<BorrowedMaterial>(); 
				listBorrowedMaterial.addAll(selectedDunning.getBorrowedMaterials());
				//ByteArrayOutputStream baos = PDFDunning.createFirstDunning(setStudent, listBorrowedMaterial).createByteArrayOutputStreamForPDF();
				
//				String fileNameIncludingHash = ""+ new Date().hashCode() + "_MAHNUNG_"+selectedDunning.getStudent().getFirstname()+"_"+selectedDunning.getStudent().getLastname();
				String fileNameIncludingHash = ""+ new Date().hashCode() + "_MAHNUNG_"+selectedDunning.getStudent().getFirstname();
				//StreamResource sr = new StreamResource(new PDFHandler.PDFStreamSource(baos), fileNameIncludingHash);
				
				//new PrintingComponent(sr, "Mahnung");
				selectedDunning.setStatus(Dunning.Status.SENT);
				tableDunnings.removeAllItems();
				dunningViewModel.doUpdateDunning(selectedDunning);			}
		});
		buttonSecondDunning.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 8123444488274722661L;

			@Override
			public void buttonClick(ClickEvent event) {
				Set<Student> setStudent = new HashSet<Student>();
				setStudent.add(selectedDunning.getStudent());
				List<BorrowedMaterial> listBorrowedMaterial = new ArrayList<BorrowedMaterial>(); 
				listBorrowedMaterial.addAll(selectedDunning.getBorrowedMaterials());
				//ByteArrayOutputStream baos = PDFDunning.createSecondDunning(setStudent, listBorrowedMaterial).createByteArrayOutputStreamForPDF();
				
//				String fileNameIncludingHash = ""+ new Date().hashCode() + "_MAHNUNG_"+selectedDunning.getStudent().getFirstname()+"_"+selectedDunning.getStudent().getLastname();
				String fileNameIncludingHash = ""+ new Date().hashCode() + "_MAHNUNG_"+selectedDunning.getStudent().getFirstname();
				//StreamResource sr = new StreamResource(new PDFHandler.PDFStreamSource(baos), fileNameIncludingHash);
				
				//new PrintingComponent(sr, "Mahnung");
				selectedDunning.setStatus(Dunning.Status.SENT);
				tableDunnings.removeAllItems();
				dunningViewModel.doUpdateDunning(selectedDunning);
			}
		});
	}

	private void init() {
		horizontalLayoutButtonBar = new HorizontalLayout();
		buttonSecondDunning = new Button(SECOND_DUNNING);
		buttonNewDunning = new Button(NEW_DUNNING);
		tableDunnings = new Table();

		tableDunnings.setSelectable(true);
		tableDunnings.setSizeFull();
		tableDunnings
				.addContainerProperty(TABLE_FIRST_NAME, String.class, null);
		tableDunnings.addContainerProperty(TABLE_LAST_NAME, String.class, null);
		tableDunnings.addContainerProperty(TABLE_CLASS, String.class, null);
		tableDunnings.addContainerProperty(TABLE_TYPE, String.class, null);
		setSpacing(true);
		setMargin(true);

		horizontalLayoutButtonBar.setSpacing(true);
		addListener();
	}

	private void buildLayout() {
		horizontalLayoutButtonBar.addComponent(buttonNewDunning);
		horizontalLayoutButtonBar.addComponent(buttonSecondDunning);
		addComponent(tableDunnings);
		addComponent(horizontalLayoutButtonBar);
	}


	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}

	private void bindViewModel(ViewModelComposer viewModelComposer,
			Object... viewModels) {
		try {
			viewModelComposer.bind(this, viewModels);
		} catch (IllegalAccessException | NoSuchElementException
				| UnsupportedOperationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getTitle() {
		return TITLE;
	}

}
