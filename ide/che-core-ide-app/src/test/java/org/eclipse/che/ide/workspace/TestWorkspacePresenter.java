/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.workspace;

import org.eclipse.che.ide.api.constraints.Constraints;
import org.eclipse.che.ide.api.parts.PartPresenter;
import org.eclipse.che.ide.api.parts.PartStackType;
import org.eclipse.che.ide.part.PartStackPresenter;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.inject.Provider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link WorkspacePresenter} functionality.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class TestWorkspacePresenter {
    /**
     *
     */
    private static final PartStackType TYPE = PartStackType.EDITING;

    private static final Constraints CONSTRAINT = Constraints.FIRST;

    @Mock
    Provider<PartStackPresenter> partStackProvider;

    @Mock
    Provider<WorkBenchPresenter> activePerspectiveProvider;

    @Mock
    WorkBenchPresenter activePerspective;

    @Mock
    WorkspaceView view;

    @Mock
    PartStackPresenter partStack;

    WorkspacePresenter presenter;

    @Before
    public void disarm() {
        // don't throw an exception if GWT.create() invoked
        GWTMockUtilities.disarm();

        when(partStackProvider.get()).thenReturn(partStack);
        when(activePerspectiveProvider.get()).thenReturn(activePerspective);

        presenter = new WorkspacePresenter(view, null, null, null, activePerspectiveProvider);
    }

    @After
    public void restore() {
        GWTMockUtilities.restore();
    }

    @Test
    public void shouldAddToStack() {
        PartPresenter part = mock(PartPresenter.class);
        presenter.openPart(part, TYPE, CONSTRAINT);
        // verify part added to proper stack
        verify(activePerspective).openPart(eq(part), eq(TYPE), eq(CONSTRAINT));
    }

    @Test
    public void shouldCallSetActivePart() {
        PartPresenter part = mock(PartPresenter.class);
        presenter.setActivePart(part);

        verify(activePerspective).setActivePart(eq(part));
    }
}