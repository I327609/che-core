/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.jseditor.client.texteditor;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.editor.AbstractEditorPresenter;
import org.eclipse.che.ide.api.editor.EditorInput;
import org.eclipse.che.ide.api.editor.EditorWithAutoSave;
import org.eclipse.che.ide.api.editor.EditorWithErrors;
import org.eclipse.che.ide.api.event.FileContentUpdateEvent;
import org.eclipse.che.ide.api.event.FileContentUpdateHandler;
import org.eclipse.che.ide.api.event.FileEvent;
import org.eclipse.che.ide.api.event.FileEventHandler;
import org.eclipse.che.ide.api.notification.Notification;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.api.project.tree.VirtualFile;
import org.eclipse.che.ide.api.texteditor.HandlesTextOperations;
import org.eclipse.che.ide.api.texteditor.HandlesUndoRedo;
import org.eclipse.che.ide.api.texteditor.HasReadOnlyProperty;
import org.eclipse.che.ide.api.texteditor.TextEditorOperations;
import org.eclipse.che.ide.api.texteditor.UndoableEditor;
import org.eclipse.che.ide.api.texteditor.outline.OutlineModel;
import org.eclipse.che.ide.debug.BreakpointManager;
import org.eclipse.che.ide.debug.BreakpointRenderer;
import org.eclipse.che.ide.debug.HasBreakpointRenderer;
import org.eclipse.che.ide.jseditor.client.JsEditorConstants;
import org.eclipse.che.ide.jseditor.client.codeassist.CodeAssistProcessor;
import org.eclipse.che.ide.jseditor.client.codeassist.CodeAssistantFactory;
import org.eclipse.che.ide.jseditor.client.codeassist.CompletionsSource;
import org.eclipse.che.ide.jseditor.client.debug.BreakpointRendererFactory;
import org.eclipse.che.ide.jseditor.client.document.Document;
import org.eclipse.che.ide.jseditor.client.document.DocumentStorage;
import org.eclipse.che.ide.jseditor.client.document.DocumentStorage.EmbeddedDocumentCallback;
import org.eclipse.che.ide.jseditor.client.editorconfig.EditorUpdateAction;
import org.eclipse.che.ide.jseditor.client.editorconfig.TextEditorConfiguration;
import org.eclipse.che.ide.jseditor.client.events.CompletionRequestEvent;
import org.eclipse.che.ide.jseditor.client.events.DocumentReadyEvent;
import org.eclipse.che.ide.jseditor.client.events.GutterClickEvent;
import org.eclipse.che.ide.jseditor.client.events.GutterClickHandler;
import org.eclipse.che.ide.jseditor.client.filetype.FileTypeIdentifier;
import org.eclipse.che.ide.jseditor.client.formatter.ContentFormatter;
import org.eclipse.che.ide.jseditor.client.gutter.Gutters;
import org.eclipse.che.ide.jseditor.client.gutter.HasGutter;
import org.eclipse.che.ide.jseditor.client.keymap.Keybinding;
import org.eclipse.che.ide.jseditor.client.position.PositionConverter;
import org.eclipse.che.ide.jseditor.client.quickfix.QuickAssistantFactory;
import org.eclipse.che.ide.jseditor.client.reconciler.Reconciler;
import org.eclipse.che.ide.jseditor.client.reconciler.ReconcilerWithAutoSave;
import org.eclipse.che.ide.jseditor.client.text.LinearRange;
import org.eclipse.che.ide.jseditor.client.text.TextPosition;
import org.eclipse.che.ide.jseditor.client.text.TextRange;
import org.eclipse.che.ide.jseditor.client.texteditor.EmbeddedTextEditorPartView.Delegate;
import org.eclipse.che.ide.hotkeys.HasHotKeyItems;
import org.eclipse.che.ide.hotkeys.HotKeyItem;
import org.eclipse.che.ide.rest.AsyncRequestLoader;
import org.eclipse.che.ide.texteditor.selection.CursorModelWithHandler;
import org.eclipse.che.ide.ui.dialogs.CancelCallback;
import org.eclipse.che.ide.ui.dialogs.ConfirmCallback;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.vectomatic.dom.svg.ui.SVGResource;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.eclipse.che.ide.api.notification.Notification.Type.ERROR;


/**
 * Presenter part for the embedded variety of editor implementations.
 */
public class EmbeddedTextEditorPresenter<T extends EditorWidget> extends AbstractEditorPresenter
        implements EmbeddedTextEditor,
                   FileEventHandler,
                   UndoableEditor,
                   HasBreakpointRenderer,
                   HasReadOnlyProperty,
                   HandlesTextOperations,
                   EditorWithAutoSave,
                   EditorWithErrors,
                   HasHotKeyItems,
                   Delegate {

    /** File type used when we have no idea of the actual content type. */
    public final static String DEFAULT_CONTENT_TYPE = "text/plain";

    private final Resources              resources;
    private final WorkspaceAgent         workspaceAgent;
    private final EditorWidgetFactory<T> editorWidgetFactory;
    private final EditorModule<T>        editorModule;
    private final JsEditorConstants      constant;

    private final DocumentStorage            documentStorage;
    private final EventBus                   generalEventBus;
    private final FileTypeIdentifier         fileTypeIdentifier;
    private final DialogFactory              dialogFactory;
    private final CodeAssistantFactory       codeAssistantFactory;
    private final QuickAssistantFactory      quickAssistantFactory;
    private final BreakpointManager          breakpointManager;
    private final EmbeddedTextEditorPartView editorView;
    /** The editor handle for this editor. */
    private final EditorHandle handle = new EditorHandle() {
    };
    private List<EditorUpdateAction> updateActions;
    private TextEditorConfiguration  configuration;
    private EditorWidget             editorWidget;
    private Document                 document;
    private CursorModelWithHandler   cursorModel;
    private HasKeybindings keyBindingsManager = new TemporaryKeybindingsManager();
    private AsyncRequestLoader  loader;
    private NotificationManager notificationManager;
    private OutlineImpl         outline;
    /** The editor's error state. */
    private EditorState         errorState;
    private boolean delayedFocus = false;
    private boolean isFocused    = false;
    private BreakpointRendererFactory breakpointRendererFactory;
    private BreakpointRenderer        breakpointRenderer;
    private List<String>              fileTypes;

    @AssistedInject
    public EmbeddedTextEditorPresenter(final CodeAssistantFactory codeAssistantFactory,
                                       final BreakpointManager breakpointManager,
                                       final BreakpointRendererFactory breakpointRendererFactory,
                                       final DialogFactory dialogFactory,
                                       final DocumentStorage documentStorage,
                                       final JsEditorConstants constant,
                                       @Assisted final EditorWidgetFactory<T> editorWidgetFactory,
                                       final EditorModule<T> editorModule,
                                       final EmbeddedTextEditorPartView editorView,
                                       final EventBus eventBus,
                                       final FileTypeIdentifier fileTypeIdentifier,
                                       final QuickAssistantFactory quickAssistantFactory,
                                       final Resources resources,
                                       final WorkspaceAgent workspaceAgent) {

        this.breakpointManager = breakpointManager;
        this.breakpointRendererFactory = breakpointRendererFactory;
        this.codeAssistantFactory = codeAssistantFactory;
        this.constant = constant;
        this.dialogFactory = dialogFactory;
        this.documentStorage = documentStorage;
        this.editorView = editorView;
        this.editorModule = editorModule;
        this.editorWidgetFactory = editorWidgetFactory;
        this.fileTypeIdentifier = fileTypeIdentifier;
        this.generalEventBus = eventBus;
        this.quickAssistantFactory = quickAssistantFactory;
        this.resources = resources;
        this.workspaceAgent = workspaceAgent;

        this.editorView.setDelegate(this);
        eventBus.addHandler(FileEvent.TYPE, this);
    }

    @Override
    protected void initializeEditor() {
        new TextEditorInit<T>(configuration,
                              generalEventBus,
                              this.codeAssistantFactory,
                              this.quickAssistantFactory,
                              this).init();

        // Postpone setting a document to give the time for editor (TextEditorViewImpl) to fully construct itself.
        // Otherwise, the editor may not be ready to render the document.
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                if (editorModule.isError()) {
                    displayErrorPanel(constant.editorInitErrorMessage());
                    return;
                }
                final boolean moduleReady = editorModule.isReady();
                EditorInitCallback<T> dualCallback = new EditorInitCallback<T>(moduleReady, loader, constant) {
                    @Override
                    public void onReady(final String content) {
                        createEditor(content);
                    }

                    @Override
                    public void onError() {
                        displayErrorPanel(constant.editorInitErrorMessage());
                    }

                    @Override
                    public void onFileError() {
                        displayErrorPanel(constant.editorFileErrorMessage());
                    }
                };
                documentStorage.getDocument(input.getFile(), dualCallback);
                if (!moduleReady) {
                    editorModule.waitReady(dualCallback);
                }
            }
        });
    }

    private void createEditor(final String content) {
        this.fileTypes = detectFileType(getEditorInput().getFile());
        this.editorWidget = editorWidgetFactory.createEditorWidget(fileTypes);

        // finish editor initialization
        this.editorView.setEditorWidget(this.editorWidget);

        this.document = this.editorWidget.getDocument();
        this.document.setFile(input.getFile());
        this.cursorModel = new EmbeddedEditorCursorModel(this.document);

        this.editorWidget.setTabSize(this.configuration.getTabWidth());

        // initialize info panel
        this.editorView.initInfoPanel(this.editorWidget.getMode(), this.editorWidget.getEditorType(),
                                      this.editorWidget.getKeymap(), this.document.getLineCount(),
                                      this.configuration.getTabWidth());

        // handle delayed focus
        // should also check if I am visible, but how ?
        if (delayedFocus) {
            this.editorWidget.setFocus();
            this.delayedFocus = false;
        }

        // delayed keybindings creation ?
        switchHasKeybinding();

        this.editorWidget.setValue(content);
        this.generalEventBus.fireEvent(new DocumentReadyEvent(this.getEditorHandle(), this.document));

        final OutlineImpl outline = getOutline();
        if (outline != null) {
            outline.bind(this.cursorModel, this.document);
        }

        firePropertyChange(PROP_INPUT);

        setupEventHandlers();
        setupFileContentUpdateHandler();
    }

    private void setupEventHandlers() {
        this.editorWidget.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(final ChangeEvent event) {
                handleDocumentChanged();
            }
        });
        this.editorWidget.addGutterClickHandler(new GutterClickHandler() {
            @Override
            public void onGutterClick(final GutterClickEvent event) {
                if (Gutters.BREAKPOINTS_GUTTER.equals(event.getGutterId())
                    || Gutters.LINE_NUMBERS_GUTTER.equals(event.getGutterId())) {
                    breakpointManager.changeBreakPointState(event.getLineNumber());
                }
            }
        });
    }

    private void setupFileContentUpdateHandler() {
        this.generalEventBus.addHandler(FileContentUpdateEvent.TYPE, new FileContentUpdateHandler() {
            @Override
            public void onFileContentUpdate(final FileContentUpdateEvent event) {
                if (event.getFilePath() != null && event.getFilePath().equals(document.getFile().getPath())) {
                    updateContent();
                }
            }
        });
    }

    private void updateContent() {
        /* -save current cursor and (ideally) viewport
         * -set editor content which is also expected to
         *     -reset dirty flag
         *     -clear history
         * -restore current cursor position
         */
        final TextPosition currentCursor = getCursorPosition();
        this.documentStorage.getDocument(document.getFile(), new EmbeddedDocumentCallback() {

            @Override
            public void onDocumentReceived(final String content) {
                editorWidget.setValue(content);
                document.setCursorPosition(currentCursor);
            }

            @Override
            public void onDocumentLoadFailure(final Throwable caught) {
                displayErrorPanel(constant.editorFileErrorMessage());
            }
        });
    }

    private void displayErrorPanel(final String message) {
        this.editorView.showPlaceHolder(new Label(message));
    }

    private void handleDocumentChanged() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                updateDirtyState(editorWidget.isDirty());
            }
        });
    }

    @Override
    public void close(final boolean save) {
        this.documentStorage.documentClosed(this.document);
    }

    @Inject
    public void injectAsyncLoader(final AsyncRequestLoader loader) {
        this.loader = loader;
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public void doRevertToSaved() {
        // do nothing
    }

    @Override
    public OutlineImpl getOutline() {
        if (outline != null) {
            return outline;
        }
        final OutlineModel outlineModel = getConfiguration().getOutline();
        if (outlineModel != null) {
            outline = new OutlineImpl(resources, outlineModel);
            return outline;
        } else {
            return null;
        }
    }

    @NotNull
    protected Widget getWidget() {
        return this.editorView.asWidget();
    }

    @Override
    public void go(final AcceptsOneWidget container) {
        container.setWidget(getWidget());
    }

    @Override
    public String getTitleToolTip() {
        return null;
    }

    @Override
    public void onClose(@NotNull final AsyncCallback<Void> callback) {
        if (isDirty()) {
            dialogFactory.createConfirmDialog(
                    constant.askWindowCloseTitle(),
                    constant.askWindowSaveChangesMessage(getEditorInput().getName()),
                    new ConfirmCallback() {
                        @Override
                        public void accepted() {
                            doSave();
                            handleClose();
                            callback.onSuccess(null);
                        }
                    },
                    new CancelCallback() {
                        @Override
                        public void cancelled() {
                            handleClose();
                            callback.onSuccess(null);
                        }
                    }).show();
        } else {
            handleClose();
            callback.onSuccess(null);
        }
    }

    @Override
    public EmbeddedTextEditorPartView getView() {
        return this.editorView;
    }

    @Override
    public void activate() {
        if (editorWidget != null) {
            editorWidget.refresh();
            editorWidget.setFocus();
        } else {
            this.delayedFocus = true;
        }
    }

    @Override
    public void onFileOperation(final FileEvent event) {
        if (event.getOperationType() != FileEvent.FileOperation.CLOSE) {
            return;
        }

        final String eventFilePath = event.getFile().getPath();
        final String filePath = input.getFile().getPath();
        if (filePath.equals(eventFilePath)) {
            workspaceAgent.removePart(this);
        }
    }

    @Override
    public void initialize(@NotNull final TextEditorConfiguration configuration,
                           @NotNull final NotificationManager notificationManager) {
        this.configuration = configuration;
        this.notificationManager = notificationManager;
    }

    @Override
    public TextEditorConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public ImageResource getTitleImage() {
        return input.getImageResource();
    }

    @Override
    public SVGResource getTitleSVGImage() {
        return input.getSVGResource();
    }

    @NotNull
    @Override
    public String getTitle() {
        return input.getName();
    }

    @Override
    public void doSave() {
        doSave(new AsyncCallback<EditorInput>() {
            @Override
            public void onSuccess(final EditorInput result) {
                // do nothing
            }

            @Override
            public void onFailure(final Throwable caught) {
                // do nothing
            }
        });
    }

    @Override
    public void doSave(final AsyncCallback<EditorInput> callback) {

        this.documentStorage.saveDocument(getEditorInput(), this.document, false, new AsyncCallback<EditorInput>() {
            @Override
            public void onSuccess(EditorInput editorInput) {
                updateDirtyState(false);
                editorWidget.markClean();
                afterSave();
                if (callback != null) {
                    callback.onSuccess(editorInput);
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                final Notification notification = new Notification(caught.getMessage(), ERROR);
                notificationManager.showNotification(notification);
                if (callback != null) {
                    callback.onFailure(caught);
                }
            }
        });
    }

    /** Override this method for handling after save actions. */
    protected void afterSave() {
    }

    @Override
    public void doSaveAs() {
        // TODO not implemented
    }

    @Override
    public HandlesUndoRedo getUndoRedo() {
        if (this.editorWidget != null) {
            return this.editorWidget.getUndoRedo();
        } else {
            return null;
        }
    }

    @Override
    public EditorState getErrorState() {
        return this.errorState;
    }

    @Override
    public void setErrorState(final EditorState errorState) {
        this.errorState = errorState;
        firePropertyChange(ERROR_STATE);
    }

    @Override
    public BreakpointRenderer getBreakpointRenderer() {
        if (this.breakpointRenderer == null && this.editorWidget != null && this instanceof HasGutter) {
            this.breakpointRenderer = this.breakpointRendererFactory.create(((HasGutter)this).getGutter(),
                                                                            this.editorWidget.getLineStyler(),
                                                                            this.document);
        }
        return this.breakpointRenderer;
    }

    @Override
    public Document getDocument() {
        return this.document;
    }

    @Override
    public String getContentType() {
        // Before the editor content is ready, the content type is not defined
        if (this.fileTypes == null || this.fileTypes.isEmpty()) {
            return null;
        } else {
            return this.fileTypes.get(0);
        }
    }

    @Override
    public TextRange getSelectedTextRange() {
        return getDocument().getSelectedTextRange();
    }

    @Override
    public LinearRange getSelectedLinearRange() {
        return getDocument().getSelectedLinearRange();
    }

    @Override
    public void showMessage(final String message) {
        this.editorWidget.showMessage(message);
    }

    @Override
    public TextPosition getCursorPosition() {
        return getDocument().getCursorPosition();
    }

    @Override
    public int getCursorOffset() {
        final TextPosition textPosition = getDocument().getCursorPosition();
        return getDocument().getIndexFromPosition(textPosition);
    }

    @Override
    public void refreshEditor() {
        if (this.updateActions != null) {
            for (final EditorUpdateAction action : this.updateActions) {
                action.doRefresh();
            }
        }
    }

    @Override
    public void addEditorUpdateAction(final EditorUpdateAction action) {
        if (action == null) {
            return;
        }
        if (this.updateActions == null) {
            this.updateActions = new ArrayList<>();
        }
        this.updateActions.add(action);
    }

    @Override
    public void addKeybinding(final Keybinding keybinding) {
        // the actual HasKeyBindings object can change, so use indirection
        getHasKeybindings().addKeybinding(keybinding);
    }

    private List<String> detectFileType(final VirtualFile file) {
        final List<String> result = new ArrayList<>();
        if (file != null) {
            // use the identification patterns
            final List<String> types = this.fileTypeIdentifier.identifyType(file);
            if (types != null && !types.isEmpty()) {
                result.addAll(types);
            }
            // use the registered media type if there is one
            final String storedContentType = file.getMediaType();
            if (storedContentType != null
                && !storedContentType.isEmpty()
                // give another chance at detection
                && !DEFAULT_CONTENT_TYPE.equals(storedContentType)) {
                result.add(storedContentType);
            }
        }

        // ultimate fallback - can't make more generic for text
        result.add(DEFAULT_CONTENT_TYPE);

        return result;
    }

    public HasTextMarkers getHasTextMarkers() {
        if (this.editorWidget != null) {
            return this.editorWidget;
        } else {
            return null;
        }
    }

    public HasKeybindings getHasKeybindings() {
        return this.keyBindingsManager;
    }

    @Override
    public CursorModelWithHandler getCursorModel() {
        return this.cursorModel;
    }

    @Override
    public PositionConverter getPositionConverter() {
        return this.editorWidget.getPositionConverter();
    }

    public void showCompletionProposals(final CompletionsSource source) {
        this.editorView.showCompletionProposals(this.editorWidget, source);
    }

    public boolean isCompletionProposalsShowing() {
        return editorWidget.isCompletionProposalsShowing();
    }

    public void showCompletionProposals() {
        this.editorView.showCompletionProposals(this.editorWidget);
    }

    public EditorHandle getEditorHandle() {
        return this.handle;
    }

    private void switchHasKeybinding() {
        final HasKeybindings current = getHasKeybindings();
        if (!(current instanceof TemporaryKeybindingsManager)) {
            return;
        }
        // change the key binding instance and add all bindings to the new one
        this.keyBindingsManager = this.editorWidget;
        final List<Keybinding> bindings = ((TemporaryKeybindingsManager)current).getbindings();
        for (final Keybinding binding : bindings) {
            this.keyBindingsManager.addKeybinding(binding);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<HotKeyItem> getHotKeys() {
        return editorWidget.getHotKeys();
    }

    @Override
    public void onResize() {
        if (this.editorWidget != null) {
            this.editorWidget.onResize();
        }
    }

    @Override
    public void editorLostFocus() {
        this.editorView.updateInfoPanelUnfocused(this.document.getLineCount());
        this.isFocused = false;
    }

    @Override
    public void editorGotFocus() {
        this.isFocused = true;
        this.editorView.updateInfoPanelPosition(this.document.getCursorPosition());
    }

    @Override
    public void editorCursorPositionChanged() {
        this.editorView.updateInfoPanelPosition(this.document.getCursorPosition());
    }

    @Override
    public boolean canDoOperation(final int operation) {
        if (TextEditorOperations.CODEASSIST_PROPOSALS == operation) {
            Map<String, CodeAssistProcessor> contentAssistProcessors = getConfiguration().getContentAssistantProcessors();
            if (contentAssistProcessors != null && !contentAssistProcessors.isEmpty()) {
                return true;
            }
        }
        if (TextEditorOperations.FORMAT == operation) {
            if (getConfiguration().getContentFormatter() != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void doOperation(final int operation) {
        switch (operation) {
            case TextEditorOperations.CODEASSIST_PROPOSALS:
                if (this.document != null) {
                    this.document.getDocumentHandle().getDocEventBus().fireEvent(new CompletionRequestEvent());
                }
                break;
            case TextEditorOperations.FORMAT:
                ContentFormatter formatter = getConfiguration().getContentFormatter();
                if (this.document != null && formatter != null) {
                    formatter.format(getDocument());
                }
                break;
            default:
                throw new UnsupportedOperationException("Operation code: " + operation + " is not supported!");
        }
    }

    @Override
    public boolean isReadOnly() {
        return this.editorWidget.isReadOnly();
    }

    @Override
    public void setReadOnly(final boolean readOnly) {
        this.editorWidget.setReadOnly(readOnly);
    }

    protected EditorWidget getEditorWidget() {
        return this.editorWidget;
    }

    public boolean isFocused() {
        return this.isFocused;
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus() {
        editorWidget.setFocus();
    }

    @Override
    public boolean isAutoSaveEnabled() {
        ReconcilerWithAutoSave autoSave = getAutoSave();
        return autoSave != null && autoSave.isAutoSaveEnabled();
    }

    private ReconcilerWithAutoSave getAutoSave() {
        Reconciler reconciler = getConfiguration().getReconciler();

        if (reconciler != null && reconciler instanceof ReconcilerWithAutoSave) {
            return ((ReconcilerWithAutoSave)reconciler);
        }
        return null;
    }

    @Override
    public void enableAutoSave() {
        ReconcilerWithAutoSave autoSave = getAutoSave();
        if (autoSave != null) {
            autoSave.enableAutoSave();
        }
    }

    @Override
    public void disableAutoSave() {
        ReconcilerWithAutoSave autoSave = getAutoSave();
        if (autoSave != null) {
            autoSave.disableAutoSave();
        }
    }
}
