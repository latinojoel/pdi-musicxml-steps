package com.latinojoel.di.ui.trans.steps.musicxml;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import com.latinojoel.di.trans.steps.musicxml.converter.MusicXMlConverterMeta;

/**
 * This class is responsible for the UI in Spoon of image converter step.
 * 
 * @author <a href="mailto:jlatino@sapo.pt">Joel Latino</a>
 * @since 1.0.0
 */
public class MusicXMLConverterDialog extends BaseStepDialog implements StepDialogInterface {

  /** for i18n purposes. **/
  private static final Class<?> PKG = MusicXMLConverterDialog.class;

  private MusicXMlConverterMeta input;

  private Label wlMusicXMLField;
  private CCombo wMusicXMLField, wConvertTypeField, wSaveFileField;

  private int middle;
  private int margin;
  private ModifyListener lsMod;

  public MusicXMLConverterDialog(Shell parent, BaseStepMeta in, TransMeta transMeta,
      String sname) {
    super(parent, in, transMeta, sname);
    this.input = (MusicXMlConverterMeta) in;
  }

  /**
   * Opens a step dialog window.
   * 
   * @return the (potentially new) name of the step
   */
  public String open() {

    final Shell parent = getParent();
    final Display display = parent.getDisplay();

    shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
    props.setLook(shell);
    setShellImage(shell, input);

    lsMod = new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        input.setChanged();
      }
    };
    changed = input.hasChanged();

    final FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    shell.setLayout(formLayout);
    shell.setText(BaseMessages.getString(PKG, "MusicXMLConverterDialog.DialogTitle"));

    middle = props.getMiddlePct();
    margin = Const.MARGIN;

    // Stepname line
    wlStepname = new Label(shell, SWT.RIGHT);
    wlStepname.setText(BaseMessages.getString(PKG, "System.Label.StepName"));
    props.setLook(wlStepname);
    fdlStepname = new FormData();
    fdlStepname.left = new FormAttachment(0, 0);
    fdlStepname.top = new FormAttachment(0, margin);
    fdlStepname.right = new FormAttachment(middle, -margin);
    wlStepname.setLayoutData(fdlStepname);
    wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wStepname.setText(stepname);
    props.setLook(wStepname);
    wStepname.addModifyListener(lsMod);
    fdStepname = new FormData();
    fdStepname.left = new FormAttachment(middle, 0);
    fdStepname.top = new FormAttachment(0, margin);
    fdStepname.right = new FormAttachment(100, 0);
    wStepname.setLayoutData(fdStepname);

    // If XML string defined in a Field
    wlMusicXMLField = new Label(shell, SWT.RIGHT);
    wlMusicXMLField.setText(BaseMessages.getString(PKG,
        "MusicXMLConverterDialog.wlMusicXMLField.Label"));
    props.setLook(wlMusicXMLField);
    final FormData fdlXMLField = new FormData();
    fdlXMLField.left = new FormAttachment(0, -margin);
    fdlXMLField.top = new FormAttachment(wStepname, margin);
    fdlXMLField.right = new FormAttachment(middle, -2 * margin);
    wlMusicXMLField.setLayoutData(fdlXMLField);

    wMusicXMLField = new CCombo(shell, SWT.BORDER | SWT.READ_ONLY);
    wMusicXMLField.setEditable(true);
    props.setLook(wMusicXMLField);
    wMusicXMLField.addModifyListener(lsMod);
    final FormData fdXMLField = new FormData();
    fdXMLField.left = new FormAttachment(middle, -margin);
    fdXMLField.top = new FormAttachment(wStepname, margin);
    fdXMLField.right = new FormAttachment(100, -margin);
    wMusicXMLField.setLayoutData(fdXMLField);
    wMusicXMLField.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent arg0) {
        getStreamFields(wMusicXMLField);
      }

      public void focusLost(FocusEvent arg0) {
        return;
      }
    });

    // Convert type field
    final Label wlConvertTypeField = new Label(shell, SWT.RIGHT);
    wlConvertTypeField.setText(BaseMessages.getString(PKG,
        "MusicXMLConverterDialog.wlConvertTypeField.Label"));
    props.setLook(wlConvertTypeField);
    final FormData fdlConvertTypeField = new FormData();
    fdlConvertTypeField.left = new FormAttachment(0, -margin);
    fdlConvertTypeField.top = new FormAttachment(wMusicXMLField, margin);
    fdlConvertTypeField.right = new FormAttachment(middle, -2 * margin);
    wlConvertTypeField.setLayoutData(fdlConvertTypeField);
    wConvertTypeField = new CCombo(shell, SWT.BORDER | SWT.READ_ONLY);
    wConvertTypeField.setEditable(true);
    props.setLook(wConvertTypeField);
    wConvertTypeField.addModifyListener(lsMod);
    final FormData fdConvertTypeField = new FormData();
    fdConvertTypeField.left = new FormAttachment(middle, -margin);
    fdConvertTypeField.top = new FormAttachment(wMusicXMLField, margin);
    fdConvertTypeField.right = new FormAttachment(100, -margin);
    wConvertTypeField.setLayoutData(fdConvertTypeField);
    wConvertTypeField.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent arg0) {
        wConvertTypeField.setItems(new String[] {"Just play", "Convert MusicXML to MIDI",
            "Convert MusicXML to MusicString"});
      }

      public void focusLost(FocusEvent arg0) {
        return;
      }
    });
    wConvertTypeField.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        final String convertType = wConvertTypeField.getText();
        wSaveFileField.setEnabled(true);
        if ("Just play".equalsIgnoreCase(convertType)) {
          wSaveFileField.setEnabled(false);
        }
      }
    });

    // Save file field
    final Label wlSaveFileField = new Label(shell, SWT.RIGHT);
    wlSaveFileField.setText(BaseMessages.getString(PKG,
        "MusicXMLConverterDialog.wlSaveFileField.Label"));
    props.setLook(wlSaveFileField);
    final FormData fdlSaveFileField = new FormData();
    fdlSaveFileField.left = new FormAttachment(0, -margin);
    fdlSaveFileField.top = new FormAttachment(wConvertTypeField, margin);
    fdlSaveFileField.right = new FormAttachment(middle, -2 * margin);
    wlSaveFileField.setLayoutData(fdlSaveFileField);
    wSaveFileField = new CCombo(shell, SWT.BORDER | SWT.READ_ONLY);
    wSaveFileField.setEditable(true);
    props.setLook(wSaveFileField);
    wSaveFileField.addModifyListener(lsMod);
    final FormData fdSaveFileField = new FormData();
    fdSaveFileField.left = new FormAttachment(middle, -margin);
    fdSaveFileField.top = new FormAttachment(wConvertTypeField, margin);
    fdSaveFileField.right = new FormAttachment(100, -margin);
    wSaveFileField.setLayoutData(fdSaveFileField);
    wSaveFileField.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent arg0) {
        getStreamFields(wSaveFileField);
      }

      public void focusLost(FocusEvent arg0) {
        return;
      }
    });


    wOK = new Button(shell, SWT.PUSH);
    wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));

    wCancel = new Button(shell, SWT.PUSH);
    wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));

    setButtonPositions(new Button[] {wOK, wCancel}, margin, null);

    // Add listeners
    lsOK = new Listener() {
      public void handleEvent(Event e) {
        ok();
      }
    };
    lsCancel = new Listener() {
      public void handleEvent(Event e) {
        cancel();
      }
    };

    wOK.addListener(SWT.Selection, lsOK);
    wCancel.addListener(SWT.Selection, lsCancel);

    lsDef = new SelectionAdapter() {
      public void widgetDefaultSelected(SelectionEvent e) {
        ok();
      }
    };

    // Detect X or ALT-F4 or something that kills this window...
    shell.addShellListener(new ShellAdapter() {
      public void shellClosed(ShellEvent e) {
        cancel();
      }
    });

    // Set the shell size, based upon previous time...
    setSize();
    getData(input);
    input.setChanged(changed);

    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    return stepname;
  }

  /**
   * Gets the values in the stream for ComboBox.
   * 
   * @param cCombo the ComboBox.
   */
  private void getStreamFields(CCombo cCombo) {
    try {
      final String source = cCombo.getText();
      cCombo.removeAll();
      final RowMetaInterface r = transMeta.getPrevStepFields(stepname);
      if (r != null && !r.isEmpty()) {
        cCombo.setItems(r.getFieldNames());
        if (source != null) {
          cCombo.setText(source);
        }
      }
    } catch (KettleException ke) {
      new ErrorDialog(shell, BaseMessages.getString(PKG,
          "SyslogMessageDialog.FailedToGetFields.DialogTitle"),
          BaseMessages.getString(PKG, "SyslogMessageDialog.FailedToGetFields.DialogMessage"), ke);
    }
  }

  /**
   * Read data and place it in the dialog.
   */
  public void getData(MusicXMlConverterMeta input) {
    wStepname.selectAll();
    wSaveFileField.setEnabled(true);
    if (input.getConvertTypeField() != null) {
      wConvertTypeField.setText(input.getConvertTypeField());
      if ("Just play".equalsIgnoreCase(input.getConvertTypeField())) {
        wSaveFileField.setEnabled(false);
      }
    }
    if (input.getMusicXMLField() != null) {
      wMusicXMLField.setText(input.getMusicXMLField());
    }
    if (input.getSaveFileField() != null) {
      wSaveFileField.setText(input.getSaveFileField());
    }
  }

  /**
   * Cancel.
   */
  private void cancel() {
    stepname = null;
    input.setChanged(backupChanged);
    dispose();
  }

  /**
   * Let the plugin know about the entered data.
   */
  private void ok() {
    if (!Const.isEmpty(wStepname.getText())) {
      stepname = wStepname.getText();
      if (Const.isEmpty(wConvertTypeField.getText())) {
        final MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
        mb.setMessage(BaseMessages.getString(PKG,
            "Git.ConvertTypeField.Mandatory.DialogMessage"));
        mb.setText(BaseMessages.getString(PKG, "System.Dialog.Error.Title"));
        mb.open();
        return;
      }
      input.setConvertTypeField(wConvertTypeField.getText());
      if (Const.isEmpty(wMusicXMLField.getText())) {
        final MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
        mb.setMessage(BaseMessages.getString(PKG,
            "Git.MusicXMLField.Mandatory.DialogMessage"));
        mb.setText(BaseMessages.getString(PKG, "System.Dialog.Error.Title"));
        mb.open();
        return;
      }
      input.setMusicXMLField(wMusicXMLField.getText());
      input.setSaveFileField(wSaveFileField.getText());
      dispose();
    }
  }
}
