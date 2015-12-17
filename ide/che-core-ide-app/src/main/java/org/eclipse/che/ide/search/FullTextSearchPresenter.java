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
package org.eclipse.che.ide.search;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.core.rest.shared.dto.ServiceError;
import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.project.gwt.client.QueryExpression;
import org.eclipse.che.api.project.shared.dto.ItemReference;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.search.presentation.FindResultPresenter;
import org.eclipse.che.ide.ui.loaders.requestLoader.IdeLoader;

import java.util.List;

/**
 * Presenter for full text search.
 *
 * @author Valeriy Svydenko
 */
@Singleton
public class FullTextSearchPresenter implements FullTextSearchView.ActionDelegate {

    private final FullTextSearchView     view;
    private final FindResultPresenter    findResultPresenter;
    private final DtoFactory             dtoFactory;
    private final IdeLoader              loader;
    private final ProjectServiceClient   projectServiceClient;

    @Inject
    public FullTextSearchPresenter(FullTextSearchView view,
                                   FindResultPresenter findResultPresenter,
                                   DtoFactory dtoFactory,
                                   IdeLoader loader,
                                   ProjectServiceClient projectServiceClient) {
        this.view = view;
        this.findResultPresenter = findResultPresenter;
        this.dtoFactory = dtoFactory;
        this.loader = loader;
        this.projectServiceClient = projectServiceClient;

        this.view.setDelegate(this);
    }

    /** Show dialog with view for searching. */
    public void showDialog() {
        view.showDialog();
        view.clearInput();
    }

    @Override
    public void search(final String text) {
        loader.show();
        QueryExpression queryExpression = new QueryExpression();
        queryExpression.setText(text + '*');
        if (!view.getFileMask().isEmpty()) {
            queryExpression.setName(view.getFileMask());
        }
        if (!view.getPathToSearch().isEmpty()) {
            queryExpression.setPath(view.getPathToSearch());
        }

        projectServiceClient.search(queryExpression).then(new Operation<List<ItemReference>>() {
            @Override
            public void apply(List<ItemReference> result) throws OperationException {
                view.close();
                findResultPresenter.handleResponse(result, text);
                loader.hide();
            }
        }).catchError(new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError arg) throws OperationException {
                loader.hide();
                view.showErrorMessage(dtoFactory.createDtoFromJson(arg.getMessage(), ServiceError.class).getMessage());
            }
        });
    }

    @Override
    public void setPathDirectory(String path) {
        view.setPathDirectory(path);
    }

    @Override
    public void setFocus() {
        view.setFocus();
    }
}
