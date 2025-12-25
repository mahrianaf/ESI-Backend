//Abrir Tabs Disponíveis
function openTab(tabId, btnElement) {

    var contents = document.getElementsByClassName("tab-content");
    for (var i = 0; i < contents.length; i++) {
        contents[i].style.display = "none";
        contents[i].classList.remove("ativo");
    }
    var btns = document.getElementsByClassName("tab-btn");
    for (var i = 0; i < btns.length; i++) {
        btns[i].classList.remove("ativo");
    }
    
    document.getElementById(tabId).style.display = "block";
    document.getElementById(tabId).classList.add("ativo");
    btnElement.classList.add("ativo");
}

//Adição de Linhas da Tabela de Serviços Realizados
function adicionarLinha() {
    const tbody = document.querySelector("#tabelaServico");
    const row = document.createElement("tr");

    row.innerHTML = `
        <td><input type="text" placeholder="" style="background-color: #fcfcfc;"></td>
        <td><input disabled></td>
        <td><input disabled></td>
        <td>
            <i class="ri-close-circle-fill" onclick="removerLinha(this)"></i>
        </td>
    `;
    tbody.appendChild(row);
}

//Adição de Linhas da Tabela de Medicamentos
function addMedicamento() {
    const tbody = document.querySelector("#tabelaReceita");
    const row = document.createElement("tr");

    row.innerHTML = `
        <td><input type="text" placeholder="" style="background-color: #fcfcfc;"></td>
        <td><input type="date" style="background-color: #fcfcfc;"></td>
        <td><input type="date" style="background-color: #fcfcfc;"></td>
        <td><input type="text" placeholder="" style="background-color: #fcfcfc;"></td>
        <td>
            <i class="ri-close-circle-fill" onclick="removerLinha(this)"></i>
        </td>
    `;
    tbody.appendChild(row);
}

//Remoção da Linha Adicionada
function removerLinha(btn) {
    const row = btn.parentNode.parentNode;
    row.parentNode.removeChild(row);
}

        