package com.kinglozzer.silverstripe.ide.highlighting;

import com.intellij.codeHighlighting.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.kinglozzer.silverstripe.psi.SilverstripePsiFile;
import org.jetbrains.annotations.NotNull;

public class SilverstripeTagTreeHighlightingPassFactory implements TextEditorHighlightingPassFactory, TextEditorHighlightingPassFactoryRegistrar {
    @Override
    public void registerHighlightingPassFactory(@NotNull TextEditorHighlightingPassRegistrar registrar, @NotNull Project project) {
        registrar.registerTextEditorHighlightingPass(this, new int[]{Pass.UPDATE_ALL}, null, false, -1);
    }

    @Override
    public TextEditorHighlightingPass createHighlightingPass(@NotNull final PsiFile file, @NotNull final Editor editor) {
        if (!(file instanceof SilverstripePsiFile)) {
            return null;
        }

        return new SilverstripeTagTreeHighlightingPass(file, editor);
    }
}
