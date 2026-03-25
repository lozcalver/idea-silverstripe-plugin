package com.kinglozzer.silverstripe.ide.templates;

import com.intellij.codeInsight.template.TemplateActionContext;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlTokenType;
import com.kinglozzer.silverstripe.SilverstripeLanguage;
import com.kinglozzer.silverstripe.ide.highlighting.SilverstripeSyntaxHighlighter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SilverstripeHTMLContextType extends TemplateContextType {
    protected SilverstripeHTMLContextType() {
        super("Silverstripe HTML");
    }

    @Override
    public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
        PsiFile file = templateActionContext.getFile();
        if (!SilverstripeLanguage.INSTANCE.is(file.getLanguage())) {
            return false;
        }

        PsiElement element = file.findElementAt(templateActionContext.getStartOffset());
        if (element == null) {
            return false;
        }

        // The context we want is when typing in an HTML part of a Silverstripe template - i.e. not already in
        // a block/injection/comment
        return element.getNode().getElementType().equals(XmlTokenType.XML_DATA_CHARACTERS);
    }

    @Nullable
    @Override
    public SyntaxHighlighter createHighlighter() {
        return new SilverstripeSyntaxHighlighter();
    }
}
