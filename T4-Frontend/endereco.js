const API_BASE_URL = "http://localhost:8080/api/endereco";

function showMessage(msg, type = 'success') {
    const statusDiv = document.getElementById('status-message');
    if (!statusDiv) return console.error('Elemento #status-message não encontrado.');

    statusDiv.textContent = msg;
    statusDiv.className = `message ${type}`;
    statusDiv.style.display = 'block';

    statusDiv.style.padding = '10px';
    statusDiv.style.marginBottom = '15px';
    statusDiv.style.borderRadius = '4px';
    statusDiv.style.backgroundColor = (type === 'success' ? '#d4edda' : (type === 'error' ? '#f8d7da' : '#fff3cd'));
    statusDiv.style.color = (type === 'success' ? '#155724' : (type === 'error' ? '#721c24' : '#856404'));

    setTimeout(() => {
        statusDiv.style.display = 'none';
    }, 5000);
}

function clearForm() {
    //Limpa campos de texto e números
    const ids = ['inputNome', 'inputCPF', 'inputCEP', 'inputComplemento', 'inputNumero', 'inputIdCidade'];
    ids.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = '';
    });
    //Reseta os selects para a primeira opção (valor 0)
    const selects = ['selectBairro', 'selectLogradouro'];
    selects.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.selectedIndex = 0;
    });
    //Limpa os botões de busca
    const buscas = ['inputCepBusca', 'inputIdBusca', 'inputCepViaCep', 'inputCpfBusca'];
    buscas.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = '';
    });
    //Limpa a mensagem de validação da cidade
    const cidadeInfo = document.getElementById('cidadeInfo');
    if (cidadeInfo) {
        cidadeInfo.textContent = '';
    }
}

function esconderResultadoBusca() {
    const resultadoDiv = document.getElementById('resultadoBusca');
    if (resultadoDiv) {
        resultadoDiv.style.display = 'none';
        resultadoDiv.innerHTML = '';
    }
}

//Mostra o resultado da buca por endereço
function formatarEnderecoParaHTML(endereco, index = 1) {
    
    const cidadeNome = endereco.cidade ? `${endereco.cidade.nomeCidade}/${endereco.cidade.uf.siglaUF || endereco.cidade.uf}` : 'N/A';
    const logradouroNome = endereco.logradouro ? `${endereco.logradouro.nomeLogradouro}` : 'N/A';
    const bairroNome = endereco.bairro ? endereco.bairro.nomeBairro : 'N/A';
    
    const titulo = `🏠 Endereço ${index} | ID: ${endereco.idEndereco}`;

    return `
        <div class="destaque">
            <span class="nome_busca">${titulo}</span>
            <strong>CEP</strong> ................................. ${endereco.cep}<br>
            <strong>Logradouro</strong> .............. ${logradouroNome}<br>
            <strong>Bairro</strong> .......................... ${bairroNome}<br>
            <strong>Cidade/UF</strong> ................. ${cidadeNome}<br>
        </div>
    `;
}

function exibirResultadoBusca(data) {
    const resultadoDiv = document.getElementById('resultadoBusca');
    resultadoDiv.innerHTML = '';
    
    let htmlContent = '';
    
    if (Array.isArray(data)) {
        data.forEach((endereco, index) => {
            htmlContent += formatarEnderecoParaHTML(endereco, index + 1);
        });
    } else if (data && data.cep) {
        htmlContent += formatarEnderecoParaHTML(data, 1);
    } 
    resultadoDiv.innerHTML = htmlContent;
    resultadoDiv.style.display = 'block';
}

//Consulta de endereço cadastrado por viacep
/** @param {string} cep */

async function consultarViaCEP(cep) {
    const url = `${API_BASE_URL}/viacep?cep=${cep}`;
    esconderResultadoBusca();

    const resultadoDiv = document.getElementById('resultadoBusca');
    resultadoDiv.innerHTML = '';
    
    let htmlContent = '';
    
    try {
        const response = await fetch(url);
        const data = await response.json();

        if (response.ok) {
            if (!data || !data.cep) {
                showMessage(`ViaCEP não encontrou: ${cep}.`, 'warning');
            } else {
                htmlContent+=`
                <div class="destaque">
                    <span class="nome_busca">🌐 Endereço ViaCEP</span>
                    <strong>CEP</strong> ................................. ${data.cep}<br>
                    <strong>Logradouro</strong> ............... ${data.logradouro.nomeLogradouro}<br>
                    <strong>Bairro</strong> ........................... ${data.bairro.nomeBairro}<br>
                    <strong>Cidade/UF</strong> .................. ${data.cidade.nomeCidade}/${data.cidade.uf.siglaUF}<br>
                </div>
                `;
                resultadoDiv.innerHTML = htmlContent;
                resultadoDiv.style.display = 'block';
                
                showMessage('✅ ViaCEP realizado!', 'success');
                clearForm();
            }
        } else {
            showMessage(data.message || `Erro ViaCEP. Status: ${response.status}`, 'error');
        }
    } catch (error) {
        showMessage('Erro de conexão com servidor.', 'error');
        console.error('Erro na consulta ViaCEP:', error);
    }
}

//Consultar Pessoa cadastrada pelo CPF
async function consultarPessoa() {
   
    //Parâmetros Entidade
    const urlParams = new URLSearchParams(window.location.search);
    const tipoUsuario = urlParams.get('tipo'); 

    const messageDiv = document.getElementById('resultadoBusca');
    const cpfInput = document.getElementById('inputCpfBusca').value.trim();

    if (!cpfInput) {
        showMessage('Informe o CPF!', 'warning');
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/api/endereco?cpf=${cpfInput}&pessoa=${tipoUsuario}`);
        if (response.ok) {
            const data = await response.json();

            const resumoHTML = ` 
            <div class="destaque">
                <span class="nome_busca">📑 Cadastro Completo</span>
                <strong>Nome</strong> ....................... ${data.nome}<br>
                <strong>Email</strong> ....................... ${data.emails}<br>
                <strong>CPF</strong> ............................ ${data.cpf}<br>
                <strong>Endereço</strong> ................ ${data.endereco.logradouro.nomeLogradouro}, ${data.nroMoradia}<br>
                <strong>Bairro</strong> ....................... ${data.endereco.bairro.nomeBairro}<br> 
                <strong>Cidade</strong> ..................... ${data.endereco.cidade.nomeCidade} - ${data.endereco.cidade.uf.siglaUF} 
            </div>
            `;
            messageDiv.innerHTML = resumoHTML;
            messageDiv.className = "message success"; 
            messageDiv.style.display = "block"; 
            clearForm();

        } else {
            showMessage('❌ CPF não encontrado.', 'error');
            messageDiv.style.display = "none";
        }
    } catch (error) {
        console.error(error);
        showMessage('Erro ao buscar dados.', 'error');
    }
}

//Consulta endereço cadastrado
/** @param {'cep'|'id'|'viacep'} tipo */
 
async function consultarEndereco(tipo) {
    esconderResultadoBusca(); 
    
    let url = API_BASE_URL;
    let valor = '';

    if (tipo === 'viacep') {
        valor = document.getElementById('inputCepViaCep').value.trim();
        if (!valor) return showMessage('Informe CEP do endereço para ViaCEP!', 'warning');
        return consultarViaCEP(valor);
    } 
    
    if (tipo === 'cep') {
        valor = document.getElementById('inputCepBusca').value.trim();
        if (!valor) return showMessage('Informe CEP do endereço para busca!', 'warning');
        url += `?cep=${valor}`;
    } 
    
    else if (tipo === 'id') {
        valor = document.getElementById('inputIdBusca').value.trim();
        if (!valor) return showMessage('Informe ID do endereço para busca!', 'warning');
        url += `/id/${valor}`; 
    } 
    else return;

    try {
        const response = await fetch(url);
        const data = await response.json(); 

        if (response.ok) {
            
            const arrayEmpty = Array.isArray(data) && data.length === 0;
            if (!data || arrayEmpty) {
                 return showMessage(`⚠️ ${tipo.toUpperCase()} sem endereço!`, 'warning');
            }
            exibirResultadoBusca(data); 
            
            const msgSucesso = Array.isArray(data) ? `✅ CEP: ${data.length} endereço(s).` : `✅ ID: ${data.idEndereco}.`;
            showMessage(msgSucesso, 'success');
            clearForm();
            
        } else {
            showMessage(data.message || 'Erro ao consultar endereço local!', 'error');
        }
    } catch (error) {
        showMessage('Erro de conexão com a API via Tomcat!', 'error');
        console.error('Erro na consulta:', error);
    }
}

//Validar ID Cidade no cadastro
async function validarCidade() {
    const id = document.getElementById('inputIdCidade').value;
    const info = document.getElementById('cidadeInfo');
    info.textContent = '';

    if (!id || id <= 0 || isNaN(parseInt(id))) {
        info.textContent = 'ID inválido!';
        info.style.color = '#333';
        return;
    }
    const url = `${API_BASE_URL}/cidade/${id}`;

    try {
        const response = await fetch(url);

        if (response.ok) {
            const cidade = await response.json();
            info.textContent = `✅ ${cidade.nomeCidade} - ${cidade.uf.siglaUF}`; 
            info.style.color = 'green';
        } else if (response.status === 404) {
            info.textContent = `❌ Cidade ID ${id} não encontrada!`;
            info.style.color = 'red';
        } else {
            info.textContent = `⚠️ Erro ao validar Cidade!`;
            info.style.color = 'orange';
        }
    } catch (error) {
        info.textContent = 'Erro de conexão ao validar Cidade!';
        info.style.color = 'red';
        console.error('Erro ao validar Cidade:', error);
    }
}

//Cadastrar Cliente ou Paciente
async function CadastrarPessoa(){
    //Dados Formulario
    const nome = document.getElementById('inputNome').value.trim();
    const cpf = document.getElementById('inputCPF').value.trim();
    const cep = document.getElementById('inputCEP').value.trim();
    const complemento = document.getElementById('inputComplemento').value.trim();
    const nro = parseInt(document.getElementById('inputNumero').value);
    const logradouro = parseInt(document.getElementById('selectLogradouro').value);
    const bairro = parseInt(document.getElementById('selectBairro').value);
    const idCidade = parseInt(document.getElementById('inputIdCidade').value);

    //Vetores de Email e Telefone
    const emailsNodes = document.getElementsByClassName('inputEmail');
    const emailsArray = Array.from(emailsNodes).map(input => 
                        input.value.trim()).filter(val => val !== "");

    const telefonesNodes = document.getElementsByClassName('inputTelefone');
    const telefonesArray = Array.from(telefonesNodes).map(input => 
                           input.value.trim()).filter(val => val !== "");

    //Parâmetros Entidade
    const urlParams = new URLSearchParams(window.location.search);
    const tipoUsuario = urlParams.get('tipo'); 

    //Validações dos Dados
    if (!logradouro && !bairro && !nome && !complemento) {
        return showMessage('Formulário Incompleto! Necessário preencher tudo.', 'error');
    }
    if (isNaN(idCidade) || idCidade <= 0) {
        return showMessage('ID Cidade é obrigatório e deve ser positivo!', 'error');
    }
    if (isNaN(nro) || nro <= 0) {
        return showMessage('ID Cidade é obrigatório e deve ser positivo!', 'error');
    }
    if (!cep || cep.length < 8) {
        return showMessage('O CEP é obrigatório e deve ter 8 dígitos (apenas números).', 'error');
    }
    if (!cpf || cpf.length < 11) {
        return showMessage('O CPF é obrigatório e deve ter 11 dígitos (apenas números).', 'error');
    }
    if (!document.getElementById('cidadeInfo').textContent.includes('✅')) {
        return showMessage('Necessário validar ID Cidade!', 'error');
    }

    //Informação Cadastro
    const cadastro = {
        acao: "cadastrarPessoa",
        tipo: tipoUsuario,
        nome:nome,
        cpf:cpf,
        cep: cep,
        complemento: complemento,
        nro: nro,
        logradouro: logradouro,
        bairro: bairro,
        cidade: idCidade,
        emails: emailsArray,
        telefones: telefonesArray
    };
    console.log(cadastro)

    try { //Encaminhamento Backend
        const response = await fetch("http://localhost:8080/api/endereco",{
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(cadastro)
        });

        const data = await response.json();

        if (response.ok) {
            showMessage(`✅ Cadastro Feito! ID: ${data.id}`, 'success');
            clearForm();
        } else {
            showMessage(data.message || 'Erro ao processar o cadastro.', 'error');
            console.error('Erro da API:', data);
        }
    } catch (error) {
        showMessage('Erro de conexão. Verifique se o Tomcat está ativo.', 'error');
        console.error('Erro na submissão:', error);
    }
}

//Adiciona contato secundário
function adicionarLinha() {
    const tbody = document.querySelector("#tabelaContatos");
    const row = document.createElement("table");

    row.innerHTML = `
    <table id="tabelaContatos" class="row">
    <tbody class="row" style="flex: 1;">
        <td style="flex: 1;"><input class="inputTelefone" type="text" style="background-color: #e7eaee;"></td>
        <td style="flex: 1;"><input class="inputEmail"    type="text" style="background-color: #e7eaee;"></td>
        <td style="flex: 0.3;">
            <i class="ri-close-circle-fill" onclick="removerLinha(this)"></i>
        </td>
    </tbody>
    </table>
    `;
    tbody.appendChild(row);
}

//Remove contato secundário
function removerLinha(btn) {
    const row = btn.parentNode.parentNode;
    row.parentNode.removeChild(row);
}

document.addEventListener('DOMContentLoaded', () => {
    // Listener para limpar apenas números em campos de CEP/ID (para inputCidade)
    const inputCidade = document.getElementById('inputIdCidade');
    const cepFields = [document.getElementById('inputCep'), document.getElementById('inputCepBusca'), document.getElementById('inputCepViaCep')];

    [inputCidade, ...cepFields].forEach(input => {
        if (input) {
            input.addEventListener('input', function (e) {
                e.target.value = e.target.value.replace(/[^0-9]/g, '');
            });
        }
    });
});