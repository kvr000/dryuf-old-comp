/*
 * Dryuf framework
 *
 * ----------------------------------------------------------------------------------
 *
 * Copyright (C) 2000-2015 Zbyněk Vyškovský
 *
 * ----------------------------------------------------------------------------------
 *
 * LICENSE:
 *
 * This file is part of Dryuf
 *
 * Dryuf is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *
 * Dryuf is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Dryuf; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * @author	2000-2015 Zbyněk Vyškovský
 * @link	mailto:kvr@matfyz.cz
 * @link	http://kvr.matfyz.cz/software/java/dryuf/
 * @link	http://github.com/dryuf/
 * @license	http://www.gnu.org/licenses/lgpl.txt GNU Lesser General Public License v3
 */

package net.dryuf.comp.devel.mvp;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.io.IOUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import net.dryuf.comp.devel.form.DevelDryufSqlForm;
import net.dryuf.core.Options;
import net.dryuf.io.FileData;
import net.dryuf.meta.ActionDef;
import net.dryuf.mvp.Presenter;


public class DevelDryufSqlPresenter extends net.dryuf.mvp.BeanFormPresenter<DevelDryufSqlForm>
{
	private PlatformTransactionManager transactionManagerDr;

	@PersistenceContext(unitName="dryuf")
	protected EntityManager		em;


	public				DevelDryufSqlPresenter(Presenter parentPresenter, Options options)
	{
		super(parentPresenter, options);

		transactionManagerDr = getCallerContext().getBeanTyped("transactionManager-dryuf", PlatformTransactionManager.class);
	}

	@Override
	public DevelDryufSqlForm	createBackingObject()
	{
		return new DevelDryufSqlForm();
	}

	public boolean			performRunSql(ActionDef action) throws IOException
	{
		DevelDryufSqlForm develDryufSqlForm = getBackingObject();
		TransactionStatus txStatus = transactionManagerDr.getTransaction(new DefaultTransactionDefinition());
		int ran = 0;
		String statement = null;
		try {
			if (develDryufSqlForm.getSqlFile() != null) {
				FileData sqlFile = getRequest().getFile(this.getFormFieldName("sqlFile"));
				for (String statement_: IOUtils.toString(sqlFile.getInputStream(), "UTF-8").split(";\\s*\n")) {
					statement = statement_;
					em.createNativeQuery(statement).executeUpdate();
					ran++;
				}
			}
			if (develDryufSqlForm.getSql() != null) {
				for (String statement_: develDryufSqlForm.getSql().split(";\\s*\n")) {
					statement = statement_;
					em.createNativeQuery(statement).executeUpdate();
					ran++;
				}
			}
		}
		catch (Exception ex) {
			transactionManagerDr.rollback(txStatus);
			throw new RuntimeException(ex.toString()+", when running "+statement, ex);
		}
		transactionManagerDr.commit(txStatus);
		this.addMessage(MSG_Info, "successfully ran "+ran+" statements");
		return true;
	}
}
